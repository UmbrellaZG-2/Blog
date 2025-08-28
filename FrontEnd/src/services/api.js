import axios from 'axios';
import config from '@/config';

// 确定当前环境
const Environment = {
  DEVELOPMENT: 'development',
  PRODUCTION: 'production'
};

// 获取当前环境配置
const currentEnv = process.env.NODE_ENV || Environment.DEVELOPMENT;
const envConfig = config[currentEnv] || config.development;

// 创建axios实例
const api = axios.create({
  baseURL: envConfig.API_BASE_URL,
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
  return api.post('/api/articles/search', { 
    keyword: keyword || '',
    page,
    size
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
  return api.get('/api/tags/get');
};

export const getTagsList = async () => {
  return api.get('/api/tags/list');
};

export const createTag = async (tagData) => {
  return api.post('/api/tags/create', tagData);
};

export const deleteTag = async (id) => {
  return api.delete(`/api/tags/delete/${id}`);
};

export const getTagStatistics = async () => {
  return api.get('/api/tags/statistics');
};

export const getArticleTags = async (articleId) => {
  return api.get(`/api/tags/article/${articleId}`);
};

export const getRecommendedTags = async (tagNames, limit = 5) => {
  return api.get('/api/tags/recommend', {
    params: {
      tags: tagNames,
      limit
    }
  });
};

export const getPopularTags = async (limit = 10) => {
  return api.get('/api/tags/popular', {
    params: {
      limit
    }
  });
};

export const addArticleTags = async (articleId, tagNames) => {
  return api.post(`/api/articles/${articleId}/tags/put`, tagNames);
};

export const deleteArticleTag = async (articleId, tagName) => {
  return api.delete(`/api/articles/${articleId}/tags/delete/${tagName}`);
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
export const addComment = async (articleId, formData) => {
  return api.post(`/api/articles/${articleId}/comments/put`, formData);
};

export const updateComment = async (commentId, commentData) => {
  return api.put(`/api/articles/comments/${commentId}/update`, commentData);
};

export const deleteComment = async (commentId) => {
  return api.delete(`/api/articles/comments/${commentId}/delete`);
};

export const getComments = async (articleId) => {
  return api.get(`/api/articles/${articleId}/comments/get`);
};

export const getAllComments = async () => {
  return api.get('/comments/article');
};

export const getCommentById = async (id) => {
  return api.get(`/comments/${id}`);
};

export const createComment = async (commentData) => {
  return api.post('/comments', commentData);
};

export const updateCommentById = async (id, commentData) => {
  return api.put(`/comments/${id}`, commentData);
};

export const deleteCommentById = async (id) => {
  return api.delete(`/comments/${id}`);
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

export const getArticleImages = async (articleId) => {
  return api.get(`/api/images/article/${articleId}/getAll`);
};

// API端点 - 文章功能相关（草稿和点赞）
export const saveDraft = async (draftData) => {
  return api.post('/api/articles/draft', draftData);
};

export const getDrafts = async (userId) => {
  return api.get(`/api/articles/drafts/${userId}`);
};

export const publishDraft = async (id) => {
  return api.post(`/api/articles/draft/${id}/publish`);
};

export const checkDraft = async (id) => {
  return api.get(`/api/articles/draft/check/${id}`);
};

export const likeArticle = async (articleId, userId = null) => {
  const data = userId ? { userId } : {};
  return api.post(`/api/articles/${articleId}/like`, data);
};

export const unlikeArticle = async (articleId, userId) => {
  return api.post(`/api/articles/${articleId}/unlike`, { userId });
};

export const getArticleLikes = async (articleId) => {
  return api.get(`/api/articles/${articleId}/likes`);
};

export const checkUserLikedArticle = async (articleId, userId) => {
  return api.get(`/api/articles/${articleId}/like/check`, {
    params: { userId }
  });
};

export const getUserLikedArticles = async (userId, limit = 10) => {
  return api.get(`/api/articles/user/${userId}/likes`, {
    params: { limit }
  });
};

// API端点 - 认证相关
export const getAuthInfo = async () => {
  return api.get('/api/auth/get');
};

export const sendVerificationCode = async (userData) => {
  return api.post('/api/auth/register/send-code', userData);
};

export const verifyAndRegister = async (verificationData) => {
  return api.post('/api/auth/register/verify', verificationData);
};

// API端点 - 首页相关
export const getHomeData = async () => {
  return api.get('/api/home');
};

export const getAboutMe = async () => {
  return api.get('/api/home/aboutMe');
};

export default api;