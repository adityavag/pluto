const { Sequelize } = require('sequelize');
require('dotenv').config();

const logger = {
    info: (msg) => console.log(`[Database] [${new Date().toISOString()}] INFO: ${msg}`),
    error: (msg, err) => console.error(`[Database Error] [${new Date().toISOString()}] ERROR: ${msg}`, err || ''),
    query: (sql) => console.log(`[Database Query] [${new Date().toISOString()}] ${sql}`)
};

const loggingOption = process.env.DB_LOGGING === 'true' ? logger.query : false;

let sequelize;

if (process.env.DATABASE_URL) {
    sequelize = new Sequelize(process.env.DATABASE_URL, {
        dialect: 'postgres',
        logging: loggingOption,
        dialectOptions: {
            ssl: process.env.DB_SSL === 'true' ? {
                require: true,
                rejectUnauthorized: false
            } : false
        }
    });
} else {
    const dbName = process.env.DB_NAME;
    const dbUser = process.env.DB_USER;
    const dbPassword = process.env.DB_PASSWORD;
    const dbHost = process.env.DB_HOST;
    const dbPort = process.env.DB_PORT;

    sequelize = new Sequelize(dbName, dbUser, dbPassword, {
        host: dbHost,
        port: dbPort,
        dialect: 'postgres',
        logging: loggingOption,
        pool: {
            max: 5,
            min: 0,
            acquire: 30000,
            idle: 10000
        }
    });
}

const connectDB = async () => {
    try {
        await sequelize.authenticate();
        logger.info('PostgreSQL database connection established successfully.');
    } catch (error) {
        logger.error('Unable to connect to the database:', error);
        process.exit(1);
    }
};

module.exports = {
    sequelize,
    connectDB
};
