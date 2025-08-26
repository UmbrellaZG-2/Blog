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
export const getArticles = async (params = {}) => {
  // 确保参数是基本类型而不是对象
  const queryParams = {};
  if (params.page !== undefined) queryParams.page = params.page;
  if (params.size !== undefined) queryParams.size = params.size;
  
  return api.get('/api/articles', { params: queryParams });
};

export const searchArticles = async (keyword, page = 0, size = 10) => {
  return api.get('/api/articles/search', { 
    params: { 
      keyword: keyword || '',
      page,
      size
    } 
  });
};

/**
 * 使用POST方式搜索文章，避免URL中出现复杂对象
 * @param {Object} searchParams - 搜索参数对象
 * @returns {Promise} Axios请求结果
 */
export const searchArticlesByDTO = async (searchParams) => {
  return api.post('/api/articles/search', searchParams);
};

export const getArticleById = async (id) => {
  return api.get(`/api/articles/get/${id}`);
};

export const createArticle = async (articleData) => {
  return api.post('/api/articles/create', articleData);
};

export const updateArticle = async (id, articleData) => {
  return api.put(`/api/articles/update/${id}`, articleData);
};

export const deleteArticle = async (id) => {
  return api.delete(`/api/articles/delete/${id}`);
};

export const getArticlesByCategory = async (category, page = 0, size = 10) => {
  return api.get(`/api/articles/category/get/${category}`, {
    params: {
      page,
      size
    }
    });
};

// API端点 - 附件相关
export const getAttachments = async (articleId) => {
  return api.get(`/api/attachments/article/get/${articleId}`);
};

export const downloadAttachment = async (attachmentId) => {
  // 返回文件blob
  const response = await api.get(`/api/attachments/download/${attachmentId}`, {
    responseType: 'blob'
  });
  return response.data;
};

export const uploadAttachment = async (formData) => {
  return api.post('/api/attachments/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const deleteAttachment = async (attachmentId) => {
  return api.delete(`/api/attachments/delete/${attachmentId}`);
};

// API端点 - 分类和标签相关
export const getCategories = async () => {
  return api.get('/api/articles/categories/get');
};

export const getTags = async () => {
  // 假设后端有获取标签的接口
  return api.get('/api/tags/get');
};

// API端点 - 认证相关
export const login = async (credentials) => {
  return api.post('/api/auth/login', credentials);
};

export const adminLogin = async (credentials) => {
  return api.post('/api/auth/admin/login', credentials);
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
  return api.post(`/api/articles/${articleId}/comments/put`, commentData);
};

export const getComments = async (articleId) => {
  return api.get(`/api/articles/${articleId}/comments/get`);
};

// API端点 - 图片相关
export const uploadCoverImage = async (articleId, imageFile) => {
  const formData = new FormData();
  formData.append('image', imageFile);
  return api.post(`/api/images/article/${articleId}/cover/update`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const getCoverImage = async (articleId) => {
  return api.get(`/api/images/article/${articleId}/cover/get`);
};

export const deleteCoverImage = async (articleId) => {
  return api.delete(`/api/images/article/${articleId}/cover/delete`);
};

export default api;