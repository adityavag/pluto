import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, User } from 'lucide-react';

export default function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="h-[50px] bg-lc-navbar border-b border-lc-navbar-border flex items-center px-6 flex-shrink-0 z-50">
      {/* Left: Logo + nav links */}
      <div className="flex items-center gap-6">
        <Link to="/" className="flex items-center gap-2">
          <span className="text-lg font-semibold text-lc-text-primary tracking-tight">
            Pluto
          </span>
        </Link>

        <div className="flex items-center gap-4">
          <Link
            to="/"
            className={`text-sm transition-colors ${
              isActive('/')
                ? 'text-lc-text-primary'
                : 'text-lc-text-secondary hover:text-lc-text-primary'
            }`}
          >
            Problems
          </Link>
        </div>
      </div>

      {/* Right: Auth */}
      <div className="ml-auto flex items-center gap-4">
        {isAuthenticated ? (
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-1.5 text-sm text-lc-text-secondary">
              <User size={14} />
              <span>{user?.username || 'User'}</span>
            </div>
            <button
              onClick={logout}
              className="flex items-center gap-1 text-sm text-lc-text-tertiary hover:text-lc-text-secondary transition-colors"
            >
              <LogOut size={14} />
            </button>
          </div>
        ) : (
          <div className="flex items-center gap-3">
            <Link
              to="/login"
              className="text-sm text-lc-text-secondary hover:text-lc-text-primary transition-colors"
            >
              Sign In
            </Link>
            <Link
              to="/register"
              className="text-sm bg-lc-accent hover:bg-lc-accent-hover text-black font-medium px-3 py-1 rounded-md transition-colors"
            >
              Sign Up
            </Link>
          </div>
        )}
      </div>
    </nav>
  );
}
