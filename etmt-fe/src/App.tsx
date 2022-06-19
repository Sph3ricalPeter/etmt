import { Layout } from 'antd';
import { Footer } from 'antd/lib/layout/layout';
import { FunctionComponent, useState } from 'react';
import {
  Route,
  Routes,
  useLocation,
  useNavigate
} from 'react-router-dom';
import './App.css';
import { AuthProvider, RequireAuth } from './components/auth/Auth';
import Login from './components/auth/Login';
import Signup from './components/auth/Signup';
import NavHeader from './components/common/NavHeader';
import Library from './components/library/Library';

const App: FunctionComponent = () => {
  const [collapsed, setCollapsed] = useState<boolean>(false);
  const navigate = useNavigate();
  const location = useLocation();

  const state = location.state as any;
  const from = state?.from?.pathname || "/";

  const year = new Date().getFullYear();

  const toggleMenu = () => {
    setCollapsed(!collapsed);
  };

  const handleSignIn = () => {
    navigate(from, { replace: true });
  }

  const handleSignOut = () => {
    navigate("/login");
  }

  const handleSignUp = () => {
    navigate("/login");
  }

  return (
    <AuthProvider>
      <NavHeader onSignOut={handleSignOut} />
      <Layout >
        <Routes>
          <Route path="/" element={
            <RequireAuth>
              <Library />
            </RequireAuth>}
          />
          <Route path="/login" element={<Login onSignIn={handleSignIn} />} />
          <Route path="/signup" element={<Signup onSignUp={handleSignUp} />} />
        </Routes>
        <Footer style={{ textAlign: 'center' }}><b>ETMT</b> ©{year} Petr Ježek</Footer>
      </Layout>
    </AuthProvider>
  );
}

export default App;
