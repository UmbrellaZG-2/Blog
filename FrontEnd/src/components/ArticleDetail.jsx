import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Calendar, Clock, Tag, Download, ArrowLeft, ThumbsUp } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

const ArticleDetail = ({ article }) => {
  const navigate = useNavigate();
  const [likes, setLikes] = useState(article?.likes || 0);
  const [isLiked, setIsLiked] = useState(false);

  if (!article) {
    return <div>文章未找到</div>;
  }

  // 确保tags是数组类型
  const safeTags = Array.isArray(article.tags) ? article.tags : [];
  // 确保attachments是数组类型
  const safeAttachments = Array.isArray(article.attachments) ? article.attachments : [];

  const handleLike = () => {
    if (isLiked) {
      setLikes(likes - 1);
    } else {
      setLikes(likes + 1);
    }
    setIsLiked(!isLiked);
  };

  const handleDownload = (attachment) => {
    // 直接触发下载，使用浏览器默认下载路径
    toast.info(`正在下载 ${attachment.name}`);
    // 模拟下载过程
    setTimeout(() => {
      toast.success(`${attachment.name} 下载成功`);
      // 实际应用中这里会调用下载API
    }, 1500);
  };

  return (
    <div className="max-w-4xl mx-auto">
      <Button 
        variant="outline" 
        className="mb-6 flex items-center"
        onClick={() => navigate('/')}
      >
        <ArrowLeft className="w-4 h-4 mr-2" />
        返回首页
      </Button>
      
      <article className="prose prose-lg max-w-none">
        <h1 className="text-3xl font-bold mb-4">{article.title}</h1>
        
        <div className="flex flex-wrap items-center gap-4 mb-6 text-gray-600">
          <div className="flex items-center">
            <Calendar className="w-4 h-4 mr-1" />
            {new Date(article.createdAt).toLocaleDateString()}
          </div>
          <div className="flex items-center">
            <Clock className="w-4 h-4 mr-1" />
            {article.readTime}分钟阅读
          </div>
          <div className="flex items-center">
            <span className="font-medium">{article.author}</span>
          </div>
          <div className="flex items-center">
            <Button 
              variant="outline" 
              size="sm" 
              className={`flex items-center ${isLiked ? 'text-red-500 border-red-500' : ''}`}
              onClick={handleLike}
            >
              <ThumbsUp className={`w-4 h-4 mr-1 ${isLiked ? 'fill-current' : ''}`} />
              {likes}
            </Button>
          </div>
        </div>
        
        {article.coverImage && (
          <img 
            src={article.coverImage} 
            alt={article.title} 
            className="w-full h-96 object-cover rounded-lg mb-8"
          />
        )}
        
        <div className="mb-8">
          <div className="flex flex-wrap gap-2 mb-6">
            {safeTags.map((tag) => (
              <span key={tag.id} className="flex items-center text-sm bg-blue-100 text-blue-800 px-3 py-1 rounded-full">
                <Tag className="w-3 h-3 mr-1" />
                {tag.name}
              </span>
            ))}
          </div>
        </div>
        
        <div 
          className="mb-8"
          dangerouslySetInnerHTML={{ __html: article.content }} 
        />
        
        {safeAttachments.length > 0 && (
          <div>
            {/* 将按钮移动到附件上方 */}
            <div className="flex justify-center mb-6">
              <Button 
                variant="outline" 
                className="justify-center whitespace-nowrap text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 border border-input bg-background hover:bg-accent hover:text-accent-foreground h-9 rounded-md px-3 flex items-center"
              >
                0
              </Button>
            </div>
            
            <Card className="mb-8">
              <CardContent className="pt-6">
                <h3 className="text-xl font-semibold mb-4">附件</h3>
                <div className="space-y-3">
                  {safeAttachments.map((attachment) => (
                    <div key={attachment.id} className="flex items-center justify-between p-3 border rounded-lg">
                      <div>
                        <h4 className="font-medium">{attachment.name}</h4>
                        <p className="text-sm text-gray-600">{attachment.size} • 下载次数: {attachment.downloadCount}</p>
                      </div>
                      <Button 
                        variant="outline" 
                        size="sm"
                        onClick={() => handleDownload(attachment)}
                      >
                        <Download className="w-4 h-4 mr-2" />
                        下载
                      </Button>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
        )}
      </article>
    </div>
  );
};

export default ArticleDetail;
