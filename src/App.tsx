import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Products from './pages/Products';
import Orders from './pages/Orders';
import Users from './pages/Users';
import Cart from './pages/Cart';
import PrivateRoute from './components/PrivateRoute';
import Layout from './components/Layout';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-50">
          <Switch>
            <Route exact path="/login" component={Login} />
            <Route exact path="/register" component={Register} />
            <PrivateRoute>
              <Layout>
                <Switch>
                  <Route exact path="/" component={Dashboard} />
                  <Route path="/products" component={Products} />
                  <Route path="/orders" component={Orders} />
                  <Route path="/users" component={Users} />
                  <Route path="/cart" component={Cart} />
                </Switch>
              </Layout>
            </PrivateRoute>
          </Switch>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;