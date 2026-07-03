const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/db');

const slugify = (text) => {
  return text
    .toString()
    .toLowerCase()
    .trim()
    .replace(/\s+/g, '-')           // Replace spaces with -
    .replace(/[^\w\-]+/g, '')       // Remove all non-word chars
    .replace(/\-\-+/g, '-')         // Replace multiple - with single -
    .replace(/^-+/, '')             // Trim - from start of text
    .replace(/-+$/, '');            // Trim - from end of text
};

const Problem = sequelize.define('Problem', {
  problemId: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
    allowNull: false
  },
  slug: {
    type: DataTypes.STRING,
    unique: true,
    allowNull: false,
    validate: {
      notEmpty: true
    }
  },
  title: {
    type: DataTypes.STRING,
    allowNull: false,
    validate: {
      notEmpty: true
    }
  },
  description: {
    type: DataTypes.TEXT,
    allowNull: false,
    validate: {
      notEmpty: true
    }
  },
  difficulty: {
    type: DataTypes.ENUM('easy', 'medium', 'hard'),
    allowNull: false,
    validate: {
      isIn: [['easy', 'medium', 'hard']]
    }
  }
}, {
  tableName: 'problems',
  timestamps: true,
  hooks: {
    beforeValidate: (problem) => {
      if (problem.title && !problem.slug) {
        problem.slug = slugify(problem.title);
      }
    },
    beforeUpdate: (problem) => {
      if (problem.changed('title') && !problem.changed('slug')) {
        problem.slug = slugify(problem.title);
      }
    }
  }
});

module.exports = Problem;
