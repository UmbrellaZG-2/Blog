import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // 使用环境变量或默认值
  timeout: 10000,
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 添加认证token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API Error:', error);
    // 处理认证错误
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// API端点

export const getArticleById = async (id) => {
  return api.get(`/articles/${id}`);
};

export const getCategories = async () => {
  return api.get('/categories');
};

export const getTags = async () => {
  return api.get('/tags');
};

export const searchArticles = async (keyword) => {
  return api.get('/articles/search', { params: { keyword } });
};

export const getAttachments = async (articleId) => {
  return api.get(`/articles/${articleId}/attachments`);
};

export const downloadAttachment = async (attachmentId) => {
  return api.get(`/attachments/${attachmentId}/download`, { responseType: 'blob' });
};

// 认证相关API

export const login = async (credentials) => {
  const response = await api.post('/auth/login', credentials);
  if (response.token) {
    localStorage.setItem('token', response.token);
  }
  return response;
};

export const register = async (userData) => {
  return api.post('/auth/register', userData);
};

export const logout = () => {
  localStorage.removeItem('token');
};

// 文章管理API

export const createArticle = async (articleData) => {
  return api.post('/articles', articleData);
};

export const updateArticle = async (id, articleData) => {
  return api.put(`/articles/${id}`, articleData);
};

export const deleteArticle = async (id) => {
  return api.delete(`/articles/${id}`);
};

export const getArticles = async (params = {}) => {
  return api.get('/articles', { params });
};

export default api;
