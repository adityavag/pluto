import api from './axios';

export const getUserProfile = async (username) => {
  const response = await api.get(`/users/${username}`);
  return response.data;
};
