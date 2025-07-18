import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  category: string;
  brand: string;
  imageUrl: string;
  active: boolean;
}

const Products: React.FC = () => {
  const { user, token } = useAuth();
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [cart, setCart] = useState<{ [key: number]: number }>({});

  const categories = ['Smartphones', 'Laptops', 'Audio', 'Tablets', 'Gaming'];

  useEffect(() => {
    fetchProducts();
    loadCart();
  }, []);

  const fetchProducts = async () => {
    try {
      let url = '/api/products';
      const params = new URLSearchParams();
      
      if (selectedCategory) {
        params.append('category', selectedCategory);
      }
      if (searchTerm) {
        params.append('search', searchTerm);
      }
      
      if (params.toString()) {
        url += '?' + params.toString();
      }

      const response = await fetch(url, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        setProducts(data);
      }
    } catch (error) {
      console.error('Error fetching products:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const loadCart = () => {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      setCart(JSON.parse(savedCart));
    }
  };

  const saveCart = (newCart: { [key: number]: number }) => {
    localStorage.setItem('cart', JSON.stringify(newCart));
    setCart(newCart);
  };

  const addToCart = (productId: number) => {
    const newCart = { ...cart };
    newCart[productId] = (newCart[productId] || 0) + 1;
    saveCart(newCart);
  };

  const removeFromCart = (productId: number) => {
    const newCart = { ...cart };
    if (newCart[productId] > 1) {
      newCart[productId]--;
    } else {
      delete newCart[productId];
    }
    saveCart(newCart);
  };

  const handleSearch = () => {
    setIsLoading(true);
    fetchProducts();
  };

  const handleCategoryChange = (category: string) => {
    setSelectedCategory(category);
    setIsLoading(true);
    setTimeout(() => fetchProducts(), 100);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6 fade-in">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Products</h1>
        <p className="mt-2 text-gray-600">Browse our electronics collection</p>
      </div>

      {/* Filters */}
      <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1">
            <label htmlFor="search" className="block text-sm font-medium text-gray-700 mb-2">
              Search Products
            </label>
            <div className="flex">
              <input
                id="search"
                type="text"
                className="flex-1 px-3 py-2 border border-gray-300 rounded-l-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                placeholder="Search by name..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              />
              <button
                onClick={handleSearch}
                className="px-4 py-2 bg-primary-600 text-white rounded-r-md hover:bg-primary-700 transition-colors duration-200"
              >
                Search
              </button>
            </div>
          </div>
          <div>
            <label htmlFor="category" className="block text-sm font-medium text-gray-700 mb-2">
              Category
            </label>
            <select
              id="category"
              className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              value={selectedCategory}
              onChange={(e) => handleCategoryChange(e.target.value)}
            >
              <option value="">All Categories</option>
              {categories.map((category) => (
                <option key={category} value={category}>
                  {category}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Products Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {products.map((product) => (
          <div key={product.id} className="bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow duration-200">
            <img
              src={product.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image'}
              alt={product.name}
              className="w-full h-48 object-cover"
            />
            <div className="p-4">
              <div className="flex justify-between items-start mb-2">
                <span className="inline-block bg-gray-100 text-gray-800 text-xs px-2 py-1 rounded">
                  {product.category}
                </span>
                <span className="text-sm text-gray-500">{product.brand}</span>
              </div>
              <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2">{product.name}</h3>
              <p className="text-gray-600 text-sm mb-3 line-clamp-2">{product.description}</p>
              <div className="flex justify-between items-center mb-3">
                <span className="text-2xl font-bold text-primary-600">${product.price}</span>
                <span className="text-sm text-gray-500">Stock: {product.stock}</span>
              </div>
              
              {cart[product.id] ? (
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => removeFromCart(product.id)}
                      className="w-8 h-8 bg-gray-200 text-gray-700 rounded-full hover:bg-gray-300 transition-colors duration-200"
                    >
                      -
                    </button>
                    <span className="font-medium">{cart[product.id]}</span>
                    <button
                      onClick={() => addToCart(product.id)}
                      disabled={cart[product.id] >= product.stock}
                      className="w-8 h-8 bg-primary-600 text-white rounded-full hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                    >
                      +
                    </button>
                  </div>
                  <span className="text-sm text-gray-600">In cart</span>
                </div>
              ) : (
                <button
                  onClick={() => addToCart(product.id)}
                  disabled={product.stock === 0}
                  className="w-full bg-primary-600 text-white py-2 px-4 rounded-md hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                >
                  {product.stock === 0 ? 'Out of Stock' : 'Add to Cart'}
                </button>
              )}
            </div>
          </div>
        ))}
      </div>

      {products.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">No products found matching your criteria.</p>
        </div>
      )}
    </div>
  );
};

export default Products;