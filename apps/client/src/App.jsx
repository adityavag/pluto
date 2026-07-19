import { BrowserRouter, Routes, Route, Outlet } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Toaster } from 'react-hot-toast';
import Navbar from './components/Navbar';
import ProblemsPage from './pages/ProblemsPage';
import ProblemWorkspacePage from './pages/ProblemWorkspacePage';
import ProfilePage from './pages/ProfilePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';

function AppLayout() {
  return (
    <div className="h-screen flex flex-col bg-lc-bg-primary">
      <Navbar />
      <Outlet />
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Toaster
          position="top-center"
          toastOptions={{
            duration: 3000,
            style: {
              background: '#282828',
              color: '#eff1f6',
              border: '1px solid #3e3e3e',
              fontSize: '14px',
            },
          }}
        />
        <Routes>
          {/* Pages with Navbar */}
          <Route element={<AppLayout />}>
            <Route path="/" element={<ProblemsPage />} />
            <Route path="/problems/:slug" element={<ProblemWorkspacePage />} />
            <Route path="/users/:username" element={<ProfilePage />} />
          </Route>

          {/* Auth pages — no navbar */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
