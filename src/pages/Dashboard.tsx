import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

interface DashboardStats {
  totalProducts: number;
  totalOrders: number;
  totalUsers: number;
}

const Dashboard: React.FC = () => {
  const { user, token } = useAuth();
  const [stats, setStats] = useState<DashboardStats>({ totalProducts: 0, totalOrders: 0, totalUsers: 0 });
  const [recentProducts, setRecentProducts] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      // Fetch products
      const productsResponse = await fetch('/api/products', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });
      
      if (productsResponse.ok) {
        const products = await productsResponse.json();
        setRecentProducts(products.slice(0, 6));
        setStats(prev => ({ ...prev, totalProducts: products.length }));
      }

      // Fetch orders if user has permission
      if (user?.profileName === 'ADMIN') {
        const ordersResponse = await fetch('/api/orders', {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });
        
        if (ordersResponse.ok) {
          const orders = await ordersResponse.json();
          setStats(prev => ({ ...prev, totalOrders: orders.length }));
        }

        // Fetch users
        const usersResponse = await fetch('/api/users', {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });
        
        if (usersResponse.ok) {
          const users = await usersResponse.json();
          setStats(prev => ({ ...prev, totalUsers: users.length }));
        }
      }
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setIsLoading(false);
    }
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
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-2 text-gray-600">Welcome back, {user?.name}!</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200 hover:shadow-lg transition-shadow duration-200">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-blue-100 text-blue-600">
              <span className="text-2xl">📱</span>
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Total Products</p>
              <p className="text-2xl font-bold text-gray-900">{stats.totalProducts}</p>
            </div>
          </div>
        </div>

        {user?.profileName === 'ADMIN' && (
          <>
            <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200 hover:shadow-lg transition-shadow duration-200">
              <div className="flex items-center">
                <div className="p-3 rounded-full bg-green-100 text-green-600">
                  <span className="text-2xl">📦</span>
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Total Orders</p>
                  <p className="text-2xl font-bold text-gray-900">{stats.totalOrders}</p>
                </div>
              </div>
            </div>

            <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200 hover:shadow-lg transition-shadow duration-200">
              <div className="flex items-center">
                <div className="p-3 rounded-full bg-purple-100 text-purple-600">
                  <span className="text-2xl">👥</span>
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Total Users</p>
                  <p className="text-2xl font-bold text-gray-900">{stats.totalUsers}</p>
                </div>
              </div>
            </div>
          </>
        )}
      </div>

      {/* Quick Actions */}
      <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Link
            to="/products"
            className="flex items-center p-4 border-2 border-gray-200 rounded-lg hover:border-primary-300 hover:bg-primary-50 transition-all duration-200"
          >
            <span className="text-2xl mr-3">📱</span>
            <span className="font-medium text-gray-700">Browse Products</span>
          </Link>
          
          <Link
            to="/cart"
            className="flex items-center p-4 border-2 border-gray-200 rounded-lg hover:border-primary-300 hover:bg-primary-50 transition-all duration-200"
          >
            <span className="text-2xl mr-3">🛒</span>
            <span className="font-medium text-gray-700">View Cart</span>
          </Link>
          
          <Link
            to="/orders"
            className="flex items-center p-4 border-2 border-gray-200 rounded-lg hover:border-primary-300 hover:bg-primary-50 transition-all duration-200"
          >
            <span className="text-2xl mr-3">📦</span>
            <span className="font-medium text-gray-700">My Orders</span>
          </Link>

          {user?.profileName === 'ADMIN' && (
            <Link
              to="/users"
              className="flex items-center p-4 border-2 border-gray-200 rounded-lg hover:border-primary-300 hover:bg-primary-50 transition-all duration-200"
            >
              <span className="text-2xl mr-3">👥</span>
              <span className="font-medium text-gray-700">Manage Users</span>
            </Link>
          )}
        </div>
      </div>

      {/* Recent Products */}
      <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold text-gray-900">Featured Products</h2>
          <Link to="/products" className="text-primary-600 hover:text-primary-700 font-medium text-sm">
            View All →
          </Link>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {recentProducts.map((product) => (
            <div key={product.id} className="border border-gray-200 rounded-lg overflow-hidden hover:shadow-md transition-shadow duration-200">
              <img
                src={product.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image'}
                alt={product.name}
                className="w-full h-48 object-cover"
              />
              <div className="p-4">
                <h3 className="font-semibold text-gray-900 mb-1">{product.name}</h3>
                <p className="text-gray-600 text-sm mb-2">{product.brand}</p>
                <p className="text-xl font-bold text-primary-600">${product.price}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;