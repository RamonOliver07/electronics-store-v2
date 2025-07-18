import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';

interface User {
  id: number;
  email: string;
  name: string;
  active: boolean;
  profileName: string;
}

const Users: React.FC = () => {
  const { user, token } = useAuth();
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (user?.profileName === 'ADMIN') {
      fetchUsers();
    }
  }, [user]);

  const fetchUsers = async () => {
    try {
      const response = await fetch('/api/users', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      }
    } catch (error) {
      console.error('Error fetching users:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const deactivateUser = async (userId: number) => {
    if (!confirm('Are you sure you want to deactivate this user?')) {
      return;
    }

    try {
      const response = await fetch(`/api/users/${userId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        setUsers(users.map(u => 
          u.id === userId ? { ...u, active: false } : u
        ));
      }
    } catch (error) {
      console.error('Error deactivating user:', error);
    }
  };

  if (user?.profileName !== 'ADMIN') {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-semibold text-gray-900 mb-2">Access Denied</h2>
        <p className="text-gray-600">You don't have permission to view this page.</p>
      </div>
    );
  }

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
        <h1 className="text-3xl font-bold text-gray-900">User Management</h1>
        <p className="mt-2 text-gray-600">Manage system users and their access</p>
      </div>

      {/* Users Table */}
      <div className="bg-white shadow-md rounded-lg overflow-hidden border border-gray-200">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900">All Users</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Email
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Profile
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {users.map((userItem) => (
                <tr key={userItem.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center">
                        <span className="text-primary-600 font-medium">
                          {userItem.name.charAt(0).toUpperCase()}
                        </span>
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">{userItem.name}</div>
                        <div className="text-sm text-gray-500">ID: {userItem.id}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {userItem.email}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      userItem.profileName === 'ADMIN' 
                        ? 'bg-purple-100 text-purple-800' 
                        : 'bg-blue-100 text-blue-800'
                    }`}>
                      {userItem.profileName}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      userItem.active 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {userItem.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    {userItem.active && userItem.id !== user?.id && (
                      <button
                        onClick={() => deactivateUser(userItem.id)}
                        className="text-red-600 hover:text-red-700 transition-colors duration-200"
                      >
                        Deactivate
                      </button>
                    )}
                    {userItem.id === user?.id && (
                      <span className="text-gray-400">Current User</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {users.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">No users found.</p>
        </div>
      )}
    </div>
  );
};

export default Users;