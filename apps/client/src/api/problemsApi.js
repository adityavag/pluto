import api from './axios';

export const getProblems = async ({ difficulty, page = 0, size = 20 } = {}) => {
  const params = { page, size };
  if (difficulty) {
    params.difficulty = difficulty.toLowerCase();
  }
  const response = await api.get('/problems', { params });
  return response.data;
};

export const getProblemBySlug = async (slug) => {
  const response = await api.get(`/problems/slug/${slug}`);
  return response.data;
};

export const getProblemById = async (id) => {
  const response = await api.get(`/problems/${id}`);
  return response.data;
};
