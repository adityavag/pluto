const express = require('express');
const router = express.Router();
const { getAllProblems, createProblem, getProblemByIdOrSlug } = require('../controllers/problem.controller');

// Routes under /api/problems
router.get('/', getAllProblems);
router.post('/', createProblem);
router.get('/:idOrSlug', getProblemByIdOrSlug);

module.exports = router;
