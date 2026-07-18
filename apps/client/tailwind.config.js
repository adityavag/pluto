/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx}",
  ],
  theme: {
    extend: {
      colors: {
        'lc': {
          'bg-primary': '#1a1a1a',
          'bg-secondary': '#282828',
          'bg-tertiary': '#333333',
          'bg-hover': '#3e3e3e',
          'border': '#3e3e3e',
          'text-primary': '#eff1f6',
          'text-secondary': '#9b9b9b',
          'text-tertiary': '#6b6b6b',
          'accent': '#ffa116',
          'accent-hover': '#ffb84d',
          'easy': '#00b8a3',
          'medium': '#ffc01e',
          'hard': '#ff375f',
          'success': '#2cbb5d',
          'error': '#ef4743',
          'navbar': '#1a1a1a',
          'navbar-border': '#303030',
        },
      },
      fontFamily: {
        sans: [
          '-apple-system',
          'BlinkMacSystemFont',
          '"Segoe UI"',
          'Roboto',
          '"Helvetica Neue"',
          'Arial',
          'sans-serif',
        ],
      },
    },
  },
  plugins: [],
};
