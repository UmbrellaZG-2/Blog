// 后端API配置
const config = {
  // 开发环境后端地址
  development: {
    API_BASE_URL: 'http://localhost:8080'
  },
  
  // 生产环境后端地址
  production: {
    API_BASE_URL: ''
  }
};

// 根据环境变量获取配置
const env = process.env.NODE_ENV || 'development';
export default config[env];