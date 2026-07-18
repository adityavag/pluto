import api from './axios';

export const login = async ({ email, password }) => {
  const response = await api.post('/account/login', { email, password });
  return response.data;
};

export const register = async ({ username, email, password }) => {
  const response = await api.post('/account/register', {
    username,
    email,
    password,
  });
  return response.data;
};
