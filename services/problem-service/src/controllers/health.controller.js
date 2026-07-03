const getHealthStatus = (req, res) => {
  res.status(200).json({
    status: 'UP',
    service: 'problem-service',
    timestamp: new Date().toISOString()
  });
};

module.exports = {
  getHealthStatus
};
