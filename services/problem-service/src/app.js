const express = require('express');
const cors = require('cors');
const { connectDB, sequelize } = require('./config/db');
const problemRoutes = require('./routes/problem.routes');
const healthRoutes = require('./routes/health.routes');

require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 5001;

// Middlewares
app.use(cors());
app.use(express.json());

// Request logger middleware
app.use((req, res, next) => {
  const start = Date.now();
  res.on('finish', () => {
    const duration = Date.now() - start;
    console.log(`[Request] [${new Date().toISOString()}] ${req.method} ${req.originalUrl} ${res.statusCode} - ${duration}ms`);
  });
  next();
});

// Routes
app.use('/api/problems', problemRoutes);
app.use('/health', healthRoutes);

const startServer = async () => {
  // 1. Connect to PostgreSQL
  await connectDB();

  // 2. Sync models to database tables
  try {
    await sequelize.sync();
    console.log('[Database] Tables successfully synchronized.');
  } catch (error) {
    console.error('[Database Error] Table synchronization failed:', error);
  }

  // 3. Start Express server
  app.listen(PORT, () => {
    console.log(`[Server] problem-service is running on port ${PORT}`);
  });
};

startServer();
