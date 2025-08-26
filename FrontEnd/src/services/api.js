import axios from 'axios';
import config from '@/config';

// 创建axios实例
const api = axios.create({
  baseURL: config.API_BASE_URL,
  timeout: 10000,
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 可以在这里添加认证token等
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
    return Promise.reject(error);
  }
);

// API端点 - 文章相关
export const getArticles = async (params) => {
  return api.get('/articles', { params });
};

export const searchArticles = async (keyword) => {
  return api.get('/articles/search', { params: { keyword } });
};

export const getArticleById = async (id) => {
  return api.get(`/articles/get/${id}`);
};

export const createArticle = async (articleData) => {
  return api.post('/articles/create', articleData);
};

export const updateArticle = async (id, articleData) => {
  return api.put(`/articles/update/${id}`, articleData);
};

export const deleteArticle = async (id) => {
  return api.delete(`/articles/delete/${id}`);
};

export const getArticlesByCategory = async (category) => {
  return api.get(`/articles/category/get/${category}`);
};

// API端点 - 附件相关
export const getAttachments = async (articleId) => {
  return api.get(`/attachments/article/get/${articleId}`);
};

export const downloadAttachment = async (attachmentId) => {
  // 返回文件blob
  const response = await api.get(`/attachments/download/${attachmentId}`, {
    responseType: 'blob'
  });
  return response.data;
};

export const uploadAttachment = async (formData) => {
  return api.post('/attachments/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const deleteAttachment = async (attachmentId) => {
  return api.delete(`/attachments/delete/${attachmentId}`);
};

// API端点 - 分类和标签相关
export const getCategories = async () => {
  return api.get('/articles/categories/get');
};

export const getTags = async () => {
  // 假设后端有获取标签的接口
  return api.get('/tags/get');
};

// API端点 - 认证相关
export const login = async (credentials) => {
  return api.post('/auth/login', credentials);
};

export const adminLogin = async (credentials) => {
  return api.post('/auth/admin/login', credentials);
};

export const guestLogin = async () => {
  // 尝试POST方法
  try {
    return await api.post('/api/auth/guest/login');
  } catch (error) {
    // 如果POST失败，尝试GET方法
    return api.get('/api/auth/guest/login');
  }
};

// API端点 - 评论相关
export const addComment = async (articleId, commentData) => {
  return api.post(`/articles/${articleId}/comments/put`, commentData);
};

export const getComments = async (articleId) => {
  return api.get(`/articles/${articleId}/comments/get`);
};

// API端点 - 图片相关
export const uploadCoverImage = async (articleId, imageFile) => {
  const formData = new FormData();
  formData.append('image', imageFile);
  return api.post(`/images/article/${articleId}/cover/update`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const getCoverImage = async (articleId) => {
  return api.get(`/images/article/${articleId}/cover/get`);
};

export const deleteCoverImage = async (articleId) => {
  return api.delete(`/images/article/${articleId}/cover/delete`);
};

export default api;