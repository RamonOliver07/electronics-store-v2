import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';

interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}

interface Order {
  id: number;
  userId: number;
  userName: string;
  orderDate: string;
  total: number;
  status: string;
  items: OrderItem[];
}

const Orders: React.FC = () => {
  const { user, token } = useAuth();
  const [orders, setOrders] = useState<Order[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [expandedOrder, setExpandedOrder] = useState<number | null>(null);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const endpoint = user?.profileName === 'ADMIN' ? '/api/orders' : '/api/orders/my-orders';
      const response = await fetch(endpoint, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        setOrders(data);
      }
    } catch (error) {
      console.error('Error fetching orders:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const updateOrderStatus = async (orderId: number, status: string) => {
    try {
      const response = await fetch(`/api/orders/${orderId}/status`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status }),
      });

      if (response.ok) {
        setOrders(orders.map(order => 
          order.id === orderId ? { ...order, status } : order
        ));
      }
    } catch (error) {
      console.error('Error updating order status:', error);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'CONFIRMED': return 'bg-blue-100 text-blue-800';
      case 'SHIPPED': return 'bg-purple-100 text-purple-800';
      case 'DELIVERED': return 'bg-green-100 text-green-800';
      case 'CANCELLED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const toggleOrderExpansion = (orderId: number) => {
    setExpandedOrder(expandedOrder === orderId ? null : orderId);
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
        <h1 className="text-3xl font-bold text-gray-900">
          {user?.profileName === 'ADMIN' ? 'All Orders' : 'My Orders'}
        </h1>
        <p className="mt-2 text-gray-600">
          {user?.profileName === 'ADMIN' 
            ? 'Manage all customer orders' 
            : 'Track your order history and status'
          }
        </p>
      </div>

      {orders.length === 0 ? (
        <div className="text-center py-12">
          <div className="text-6xl mb-4">📦</div>
          <h2 className="text-2xl font-semibold text-gray-900 mb-2">No orders found</h2>
          <p className="text-gray-600 mb-6">
            {user?.profileName === 'ADMIN' 
              ? 'No orders have been placed yet' 
              : 'You haven\'t placed any orders yet'
            }
          </p>
          {user?.profileName !== 'ADMIN' && (
            <a
              href="/products"
              className="inline-block bg-primary-600 text-white px-6 py-3 rounded-md hover:bg-primary-700 transition-colors duration-200"
            >
              Start Shopping
            </a>
          )}
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <div key={order.id} className="bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden">
              <div className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center space-x-4">
                    <h3 className="text-lg font-semibold text-gray-900">
                      Order #{order.id}
                    </h3>
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(order.status)}`}>
                      {order.status}
                    </span>
                  </div>
                  <div className="text-right">
                    <p className="text-lg font-semibold text-primary-600">${order.total.toFixed(2)}</p>
                    <p className="text-sm text-gray-500">
                      {new Date(order.orderDate).toLocaleDateString()}
                    </p>
                  </div>
                </div>

                {user?.profileName === 'ADMIN' && (
                  <div className="mb-4">
                    <p className="text-sm text-gray-600">Customer: {order.userName}</p>
                  </div>
                )}

                <div className="flex items-center justify-between">
                  <button
                    onClick={() => toggleOrderExpansion(order.id)}
                    className="text-primary-600 hover:text-primary-700 font-medium text-sm"
                  >
                    {expandedOrder === order.id ? 'Hide Details' : 'View Details'}
                  </button>

                  {user?.profileName === 'ADMIN' && order.status !== 'DELIVERED' && order.status !== 'CANCELLED' && (
                    <div className="flex space-x-2">
                      {order.status === 'PENDING' && (
                        <button
                          onClick={() => updateOrderStatus(order.id, 'CONFIRMED')}
                          className="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors duration-200"
                        >
                          Confirm
                        </button>
                      )}
                      {order.status === 'CONFIRMED' && (
                        <button
                          onClick={() => updateOrderStatus(order.id, 'SHIPPED')}
                          className="px-3 py-1 bg-purple-600 text-white text-sm rounded hover:bg-purple-700 transition-colors duration-200"
                        >
                          Ship
                        </button>
                      )}
                      {order.status === 'SHIPPED' && (
                        <button
                          onClick={() => updateOrderStatus(order.id, 'DELIVERED')}
                          className="px-3 py-1 bg-green-600 text-white text-sm rounded hover:bg-green-700 transition-colors duration-200"
                        >
                          Deliver
                        </button>
                      )}
                      <button
                        onClick={() => updateOrderStatus(order.id, 'CANCELLED')}
                        className="px-3 py-1 bg-red-600 text-white text-sm rounded hover:bg-red-700 transition-colors duration-200"
                      >
                        Cancel
                      </button>
                    </div>
                  )}
                </div>
              </div>

              {expandedOrder === order.id && (
                <div className="border-t border-gray-200 bg-gray-50 p-6">
                  <h4 className="font-medium text-gray-900 mb-3">Order Items</h4>
                  <div className="space-y-2">
                    {order.items.map((item) => (
                      <div key={item.id} className="flex justify-between items-center py-2">
                        <div>
                          <span className="font-medium">{item.productName}</span>
                          <span className="text-gray-500 ml-2">x{item.quantity}</span>
                        </div>
                        <span className="font-medium">${(item.price * item.quantity).toFixed(2)}</span>
                      </div>
                    ))}
                  </div>
                  <div className="border-t border-gray-200 mt-4 pt-4 flex justify-between items-center">
                    <span className="font-semibold">Total</span>
                    <span className="font-semibold text-lg text-primary-600">${order.total.toFixed(2)}</span>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Orders;