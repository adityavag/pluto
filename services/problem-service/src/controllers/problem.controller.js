const Problem = require('../models/problem.model');

// Get all problems (only returns problemId, slug, title, and difficulty)
const getAllProblems = async (req, res) => {
  try {
    const problems = await Problem.findAll({
      attributes: ['problemId', 'slug', 'title', 'difficulty']
    });
    return res.status(200).json(problems);
  } catch (error) {
    console.error('Error fetching problems:', error);
    return res.status(500).json({ error: 'Internal Server Error' });
  }
};

// Create a new problem
const createProblem = async (req, res) => {
  try {
    const { title, description, difficulty } = req.body;
    
    if (!title || !description || !difficulty) {
      return res.status(400).json({ error: 'title, description, and difficulty are required.' });
    }

    const problem = await Problem.create({ title, description, difficulty });
    return res.status(201).json(problem);
  } catch (error) {
    if (error.name === 'SequelizeValidationError') {
      return res.status(400).json({ error: error.errors.map(e => e.message) });
    }
    if (error.name === 'SequelizeUniqueConstraintError') {
      return res.status(400).json({ error: 'A problem with this title already exists.' });
    }
    console.error('Error creating problem:', error);
    return res.status(500).json({ error: 'Internal Server Error' });
  }
};

// Get a single problem details by ID or Slug
const getProblemByIdOrSlug = async (req, res) => {
  try {
    const { idOrSlug } = req.params;
    
    // Check if the parameter is a sequential integer ID or slug string
    const isId = /^\d+$/.test(idOrSlug);
    const queryCondition = isId 
      ? { problemId: parseInt(idOrSlug, 10) } 
      : { slug: idOrSlug };

    const problem = await Problem.findOne({ where: queryCondition });

    if (!problem) {
      return res.status(404).json({ error: 'Problem not found' });
    }

    return res.status(200).json(problem);
  } catch (error) {
    console.error('Error fetching problem details:', error);
    return res.status(500).json({ error: 'Internal Server Error' });
  }
};

module.exports = {
  getAllProblems,
  createProblem,
  getProblemByIdOrSlug
};
