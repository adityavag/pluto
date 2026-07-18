import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function RegisterPage() {
  const navigate = useNavigate();
  const { register } = useAuth();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username || !email || !password) {
      toast.error('Please fill in all fields.');
      return;
    }
    if (password.length < 8) {
      toast.error('Password must be at least 8 characters.');
      return;
    }
    setLoading(true);
    try {
      await register({ username, email, password });
      toast.success('Account created! Please sign in.');
      navigate('/login');
    } catch (err) {
      const message =
        err.response?.data?.message || 'Registration failed. Please try again.';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-lc-bg-primary flex items-center justify-center px-4">
      <div className="w-full max-w-[400px]">
        {/* Logo */}
        <div className="text-center mb-8">
          <Link to="/" className="text-2xl font-semibold text-lc-text-primary">
            Pluto
          </Link>
          <p className="text-sm text-lc-text-tertiary mt-1">
            System Design Practice
          </p>
        </div>

        {/* Card */}
        <div className="bg-lc-bg-secondary border border-lc-border rounded-lg p-6">
          <h2 className="text-lg font-semibold text-lc-text-primary mb-6">
            Create Account
          </h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-lc-text-secondary mb-1.5">
                Username
              </label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Choose a username"
                className="w-full bg-lc-bg-primary border border-lc-border rounded-md px-3 py-2 text-sm text-lc-text-primary placeholder-lc-text-tertiary focus:outline-none focus:border-lc-accent"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-lc-text-secondary mb-1.5">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter your email"
                className="w-full bg-lc-bg-primary border border-lc-border rounded-md px-3 py-2 text-sm text-lc-text-primary placeholder-lc-text-tertiary focus:outline-none focus:border-lc-accent"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-lc-text-secondary mb-1.5">
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="At least 8 characters"
                className="w-full bg-lc-bg-primary border border-lc-border rounded-md px-3 py-2 text-sm text-lc-text-primary placeholder-lc-text-tertiary focus:outline-none focus:border-lc-accent"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-lc-accent hover:bg-lc-accent-hover text-black font-medium rounded-md py-2 text-sm transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Creating account...' : 'Sign Up'}
            </button>
          </form>

          <p className="text-center text-sm text-lc-text-tertiary mt-5">
            Already have an account?{' '}
            <Link
              to="/login"
              className="text-lc-accent hover:text-lc-accent-hover"
            >
              Sign In
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
