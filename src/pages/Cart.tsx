import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';

interface Product {
  id: number;
  name: string;
  price: number;
  imageUrl: string;
  stock: number;
}

interface CartItem {
  product: Product;
  quantity: number;
}

const Cart: React.FC = () => {
  const { token } = useAuth();
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isPlacingOrder, setIsPlacingOrder] = useState(false);
  const [orderSuccess, setOrderSuccess] = useState(false);

  useEffect(() => {
    loadCartItems();
  }, []);

  const loadCartItems = async () => {
    try {
      const savedCart = localStorage.getItem('cart');
      if (!savedCart) {
        setIsLoading(false);
        return;
      }

      const cart: { [key: number]: number } = JSON.parse(savedCart);
      const productIds = Object.keys(cart).map(id => parseInt(id));
      
      const items: CartItem[] = [];
      
      for (const productId of productIds) {
        const response = await fetch(`/api/products/${productId}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });
        
        if (response.ok) {
          const product = await response.json();
          items.push({
            product,
            quantity: cart[productId]
          });
        }
      }
      
      setCartItems(items);
    } catch (error) {
      console.error('Error loading cart items:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const updateQuantity = (productId: number, newQuantity: number) => {
    const savedCart = localStorage.getItem('cart');
    if (!savedCart) return;

    const cart: { [key: number]: number } = JSON.parse(savedCart);
    
    if (newQuantity <= 0) {
      delete cart[productId];
      setCartItems(cartItems.filter(item => item.product.id !== productId));
    } else {
      cart[productId] = newQuantity;
      setCartItems(cartItems.map(item => 
        item.product.id === productId 
          ? { ...item, quantity: newQuantity }
          : item
      ));
    }
    
    localStorage.setItem('cart', JSON.stringify(cart));
  };

  const removeItem = (productId: number) => {
    updateQuantity(productId, 0);
  };

  const getTotalPrice = () => {
    return cartItems.reduce((total, item) => total + (item.product.price * item.quantity), 0);
  };

  const placeOrder = async () => {
    if (cartItems.length === 0) return;

    setIsPlacingOrder(true);
    try {
      const orderItems = cartItems.map(item => ({
        productId: item.product.id,
        quantity: item.quantity
      }));

      const response = await fetch('/api/orders', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ items: orderItems }),
      });

      if (response.ok) {
        localStorage.removeItem('cart');
        setCartItems([]);
        setOrderSuccess(true);
        setTimeout(() => setOrderSuccess(false), 5000);
      } else {
        const error = await response.json();
        alert(error.error || 'Failed to place order');
      }
    } catch (error) {
      console.error('Error placing order:', error);
      alert('Failed to place order');
    } finally {
      setIsPlacingOrder(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (orderSuccess) {
    return (
      <div className="text-center py-12 fade-in">
        <div className="bg-green-50 border border-green-200 text-green-700 px-6 py-4 rounded-md inline-block">
          <h2 className="text-2xl font-bold mb-2">Order Placed Successfully!</h2>
          <p>Your order has been placed and is being processed.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 fade-in">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Shopping Cart</h1>
        <p className="mt-2 text-gray-600">Review and checkout your items</p>
      </div>

      {cartItems.length === 0 ? (
        <div className="text-center py-12">
          <div className="text-6xl mb-4">🛒</div>
          <h2 className="text-2xl font-semibold text-gray-900 mb-2">Your cart is empty</h2>
          <p className="text-gray-600 mb-6">Start shopping to add items to your cart</p>
          <a
            href="/products"
            className="inline-block bg-primary-600 text-white px-6 py-3 rounded-md hover:bg-primary-700 transition-colors duration-200"
          >
            Browse Products
          </a>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Cart Items */}
          <div className="lg:col-span-2 space-y-4">
            {cartItems.map((item) => (
              <div key={item.product.id} className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
                <div className="flex items-center space-x-4">
                  <img
                    src={item.product.imageUrl || 'https://via.placeholder.com/100x100?text=No+Image'}
                    alt={item.product.name}
                    className="w-20 h-20 object-cover rounded-md"
                  />
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold text-gray-900">{item.product.name}</h3>
                    <p className="text-gray-600">${item.product.price.toFixed(2)} each</p>
                    <p className="text-sm text-gray-500">Stock available: {item.product.stock}</p>
                  </div>
                  <div className="flex items-center space-x-3">
                    <button
                      onClick={() => updateQuantity(item.product.id, item.quantity - 1)}
                      className="w-8 h-8 bg-gray-200 text-gray-700 rounded-full hover:bg-gray-300 transition-colors duration-200"
                    >
                      -
                    </button>
                    <span className="font-medium text-lg">{item.quantity}</span>
                    <button
                      onClick={() => updateQuantity(item.product.id, item.quantity + 1)}
                      disabled={item.quantity >= item.product.stock}
                      className="w-8 h-8 bg-primary-600 text-white rounded-full hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                    >
                      +
                    </button>
                  </div>
                  <div className="text-right">
                    <p className="text-lg font-semibold text-gray-900">
                      ${(item.product.price * item.quantity).toFixed(2)}
                    </p>
                    <button
                      onClick={() => removeItem(item.product.id)}
                      className="text-red-600 hover:text-red-700 text-sm font-medium"
                    >
                      Remove
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Order Summary */}
          <div className="lg:col-span-1">
            <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200 sticky top-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Order Summary</h2>
              <div className="space-y-3">
                <div className="flex justify-between">
                  <span className="text-gray-600">Subtotal ({cartItems.length} items)</span>
                  <span className="font-medium">${getTotalPrice().toFixed(2)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Shipping</span>
                  <span className="font-medium">Free</span>
                </div>
                <div className="border-t border-gray-200 pt-3">
                  <div className="flex justify-between text-lg font-semibold">
                    <span>Total</span>
                    <span className="text-primary-600">${getTotalPrice().toFixed(2)}</span>
                  </div>
                </div>
              </div>
              <button
                onClick={placeOrder}
                disabled={isPlacingOrder || cartItems.length === 0}
                className="w-full mt-6 bg-primary-600 text-white py-3 px-4 rounded-md hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
              >
                {isPlacingOrder ? (
                  <span className="flex items-center justify-center">
                    <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Placing Order...
                  </span>
                ) : (
                  'Place Order'
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Cart;