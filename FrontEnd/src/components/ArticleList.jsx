import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Calendar, Tag } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import config from '@/config';

// 根据环境选择正确的API基础URL
const API_BASE_URL = process.env.NODE_ENV === 'production' 
  ? config.production.API_BASE_URL 
  : config.development.API_BASE_URL;

const ArticleList = ({ articles = [] }) => {
  const navigate = useNavigate();
  
  // 确保articles是数组类型
  const safeArticles = Array.isArray(articles) ? articles : [];

  const handleArticleClick = (id) => {
    navigate(`/article/${id}`);
  };

  // 构建封面图片URL
  const getCoverImageUrl = (coverImage, id) => {
    if (!coverImage || coverImage === 'false') {
      return null;
    }
    if (typeof coverImage === 'boolean' && coverImage === true) {
      return `${API_BASE_URL}/images/article/${id}/cover/download`;
    }
    if (coverImage === 'true') {
      return `${API_BASE_URL}/images/article/${id}/cover/download`;
    }
    if (coverImage.startsWith('http')) {
      return coverImage;
    }
    // 处理相对路径
    if (coverImage.startsWith('/')) {
      return `${API_BASE_URL}${coverImage}`;
    }
    if (coverImage.startsWith('D:\\')) {
      // 转换本地路径为URL
      const filename = coverImage.split('\\').pop();
      return `${API_BASE_URL}/images/article/${id}/cover/download`;
    }
    return coverImage;
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {safeArticles.map((article) => (
        <Card 
          key={article.id} 
          className="hover:shadow-lg transition-shadow cursor-pointer"
          onClick={() => handleArticleClick(article.id)}
        >
          {getCoverImageUrl(article.coverImage, article.id) ? (
            <img 
              src={getCoverImageUrl(article.coverImage, article.id)} 
              alt={article.title} 
              className="w-full h-48 object-cover rounded-t-lg"
            />
          ) : (
            <img 
              src="/resource/pic/cover.png" 
              alt={article.title} 
              className="w-full h-48 object-cover rounded-t-lg"
            />
          )}
          <CardHeader>
            <CardTitle className="text-xl line-clamp-2">{article.title}</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-gray-600 mb-4 line-clamp-3">{article.summary}</p>
            <div className="flex flex-wrap gap-2 mb-4">
              {Array.isArray(article.tags) && article.tags.map((tag) => (
                <span key={tag.id || tag.name} className="flex items-center text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">
                  <Tag className="w-3 h-3 mr-1" />
                  {tag.name}
                </span>
              ))}
            </div>
            <div className="flex items-center text-sm text-gray-500">
              <Calendar className="w-4 h-4 mr-1" />
              {article.createTime ? (() => {
                const date = new Date(article.createTime);
                return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
              })() : '未知时间'}
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
};

export default ArticleList;