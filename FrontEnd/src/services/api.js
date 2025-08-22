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
  return api.get(`/articles/get/${id}`);
};

// 获取文章分类
export const getArticleCategories = async () => {
  return api.get('/articles/categories/get');
};

// 获取所有标签
export const getAllTags = async () => {
  return api.get('/tags/get');
};

export const searchArticles = async (keyword) => {
  return api.get('/articles/search', { params: { keyword } });
};

export const getAttachments = async (articleId) => {
  return api.get(`/attachments/article/get/${articleId}`);
};

export const downloadAttachment = async (attachmentId) => {
  return api.get(`/attachments/download/${attachmentId}`, { responseType: 'blob' });
};

// 认证相关API

export const login = async (credentials) => {
  const response = await api.post('/auth/admin/login', credentials);
  if (response.token) {
    localStorage.setItem('token', response.token);
  }
  return response;
};

// 管理员注册接口已被禁用，不对外开放
 export const register = async (userData) => {
  throw new Error('管理员注册接口已被禁用，不对外开放');
};

// 普通用户注册接口
export const userRegister = async (userData) => {
  return api.post('/auth/register', userData);
};

export const logout = () => {
  localStorage.removeItem('token');
};

// 文章管理API

export const createArticle = async (articleData) => {
  // 构建表单数据，因为后端使用@RequestParam接收参数
  const formData = new FormData();
  formData.append('title', articleData.title);
  formData.append('category', articleData.category);
  formData.append('content', articleData.content);
  formData.append('isDraft', articleData.isDraft || false);
  
  if (articleData.tags && articleData.tags.length > 0) {
    formData.append('tags', JSON.stringify(articleData.tags));
  }
  
  return api.post('/articles/create', formData);
};

// 保存草稿
export const saveDraft = async (articleData) => {
  // 构建表单数据，因为后端使用@RequestParam接收参数
  const formData = new FormData();
  formData.append('title', articleData.title);
  formData.append('category', articleData.category);
  formData.append('content', articleData.content);
  formData.append('isDraft', true);
  
  if (articleData.tags && articleData.tags.length > 0) {
    formData.append('tags', JSON.stringify(articleData.tags));
  }
  
  return api.post('/articles/create', formData);
};

// 保存带附件的草稿
 export const saveDraftWithAttachments = async (articleData, attachments) => {
   const formData = new FormData();
   formData.append('title', articleData.title);
   formData.append('category', articleData.category);
   formData.append('content', articleData.content);
   formData.append('isDraft', true);
   
   // 添加标签
   if (articleData.tags && articleData.tags.length > 0) {
     formData.append('tags', JSON.stringify(articleData.tags));
   }
   
   // 添加封面图片
   if (articleData.coverImage) {
     formData.append('coverImage', articleData.coverImage);
   }
   
   // 添加附件
   attachments.forEach((attachment, index) => {
     formData.append(`attachments[${index}]`, attachment.file);
   });
   
   return api.post('/articles/create', formData);
 };

// 发布草稿
export const publishDraft = async (id) => {
  return api.put(`/articles/update/${id}`);
};

export const updateArticle = async (id, articleData) => {
  return api.put(`/articles/update/${id}`, articleData);
};

// 删除文章（包括清理附件）
export const deleteArticle = async (id) => {
  // 先清理附件
  await cleanupArticleAttachments(id);
  // 再删除文章
  return api.delete(`/articles/delete/${id}`);
};

// 获取文章列表
export const getArticles = async (params = {}) => {
  return api.get('/articles', { params });
};

// 删除文章标签
export const deleteArticleTag = async (articleId, tagName) => {
  return api.delete(`/articles/${articleId}/tags/delete/${tagName}`);
};

 // 添加文章标签
export const addArticleTag = async (articleId, tagName) => {
  return api.post(`/articles/${articleId}/tags/put`, { tagName });
};

 // 创建带附件文章
 export const createArticleWithAttachments = async (articleData, attachments) => {
   const formData = new FormData();
   formData.append('title', articleData.title);
   formData.append('category', articleData.category);
   formData.append('content', articleData.content);
   formData.append('isDraft', articleData.isDraft || false);
   
   // 添加标签
   if (articleData.tags && articleData.tags.length > 0) {
     formData.append('tags', JSON.stringify(articleData.tags));
   }
   
   // 添加封面图片
   if (articleData.coverImage) {
     formData.append('coverImage', articleData.coverImage);
   }
   
   // 添加附件
   attachments.forEach((attachment, index) => {
     formData.append(`attachments[${index}]`, attachment.file);
   });
   
   return api.post('/articles/create', formData);
 };

 // 更新文章附件
export const updateArticleAttachments = async (articleId, attachments) => {
  const formData = new FormData();
  
  attachments.forEach((attachment, index) => {
    formData.append(`attachments[${index}]`, attachment.file);
  });
  
  return api.put(`/articles/update/${articleId}`, formData);
};

 // 清理文章附件
export const cleanupArticleAttachments = async (articleId) => {
  return api.delete(`/articles/${articleId}/attachments`);
};

 // 游客登录
 export const guestLogin = async () => {
   const response = await api.post('/auth/guest/login');
   if (response.token) {
     localStorage.setItem('token', response.token);
   }
   return response;
 };

export default api;
