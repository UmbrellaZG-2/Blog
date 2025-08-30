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
  
  return api.get('/articles', { params: queryParams });
};

export const searchArticles = async (keyword, page = 0, size = 10) => {
  return api.post('/articles/search', { 
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
  return api.post('/articles/search', searchParams);
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

export const getArticlesByCategory = async (category, page = 0, size = 10) => {
  return api.get(`/articles/category/get/${category}`, {
    params: {
      page,
      size
    }
  });
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
  return api.get('/tags/get');
};

export const getTagsList = async () => {
  return api.get('/tags/list');
};

export const createTag = async (tagData) => {
  return api.post('/tags/create', tagData);
};

export const deleteTag = async (id) => {
  return api.delete(`/tags/delete/${id}`);
};

export const getTagStatistics = async () => {
  return api.get('/tags/statistics');
};

export const getArticleTags = async (articleId) => {
  return api.get(`/tags/article/${articleId}`);
};

export const getRecommendedTags = async (tagNames, limit = 5) => {
  return api.get('/tags/recommend', {
    params: {
      tags: tagNames,
      limit
    }
  });
};

export const getPopularTags = async (limit = 10) => {
  return api.get('/tags/popular', {
    params: {
      limit
    }
  });
};

export const addArticleTags = async (articleId, tagNames) => {
  return api.post(`/articles/${articleId}/tags/put`, tagNames);
};

export const deleteArticleTag = async (articleId, tagName) => {
  return api.delete(`/articles/${articleId}/tags/delete/${tagName}`);
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
    return await api.post('/auth/guest/login');
  } catch (error) {
    // 如果POST失败，尝试GET方法
    return api.get('/auth/guest/login');
  }
};

// API端点 - 评论相关
export const addComment = async (articleId, formData) => {
  return api.post(`/articles/${articleId}/comments/put`, formData);
};

export const updateComment = async (commentId, commentData) => {
  return api.put(`/articles/comments/${commentId}/update`, commentData);
};

export const deleteComment = async (commentId) => {
  return api.delete(`/articles/comments/${commentId}/delete`);
};

export const getComments = async (articleId) => {
  return api.get(`/articles/${articleId}/comments/get`);
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

export const getArticleImages = async (articleId) => {
  return api.get(`/images/article/${articleId}/getAll`);
};

// API端点 - 文章功能相关（草稿和点赞）
export const saveDraft = async (draftData) => {
  return api.post('/articles/draft', draftData);
};

export const getDrafts = async (userId) => {
  return api.get(`/articles/drafts/${userId}`);
};

export const publishDraft = async (id) => {
  return api.post(`/articles/draft/${id}/publish`);
};

export const checkDraft = async (id) => {
  return api.get(`/articles/draft/check/${id}`);
};

export const likeArticle = async (articleId, userId = null) => {
  const data = userId ? { userId } : {};
  return api.post(`/articles/${articleId}/like`, data);
};

export const unlikeArticle = async (articleId, userId) => {
  return api.post(`/articles/${articleId}/unlike`, { userId });
};

export const getArticleLikes = async (articleId) => {
  return api.get(`/articles/${articleId}/likes`);
};

export const checkUserLikedArticle = async (articleId, userId) => {
  return api.get(`/articles/${articleId}/like/check`, {
    params: { userId }
  });
};

export const getUserLikedArticles = async (userId, limit = 10) => {
  return api.get(`/articles/user/${userId}/likes`, {
    params: { limit }
  });
};

// API端点 - 认证相关
export const getAuthInfo = async () => {
  return api.get('/auth/get');
};

export const sendVerificationCode = async (userData) => {
  return api.post('/auth/register/send-code', userData);
};

export const verifyAndRegister = async (verificationData) => {
  return api.post('/auth/register/verify', verificationData);
};

// API端点 - 首页相关
export const getHomeData = async () => {
  return api.get('/home');
};

export const getAboutMe = async () => {
  return api.get('/home/aboutMe');
};

export default api;