import api from './axios';

export const createSubmission = async ({ problemId, excalidrawJson, writeup }) => {
  const response = await api.post('/submissions', {
    problemId,
    excalidrawJson,
    writeup,
  });
  return response.data;
};

export const getSubmission = async (id) => {
  const response = await api.get(`/submissions/${id}`);
  return response.data;
};
