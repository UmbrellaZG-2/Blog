import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Calendar, Clock, Tag, Download, ArrowLeft, ThumbsUp, MessageCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { downloadAttachment, getComments, addComment } from '@/services/api';

const ArticleDetail = ({ article }) => {
  const navigate = useNavigate();
  const [likes, setLikes] = useState(article?.likes || 0);
  const [isLiked, setIsLiked] = useState(false);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [replyingTo, setReplyingTo] = useState(null);
  const [replyContent, setReplyContent] = useState('');

  useEffect(() => {
    if (article && article.id) {
      loadComments();
    }
  }, [article]);

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

  const handleDownload = async (attachment) => {
    try {
      toast.info(`正在下载 ${attachment.name}`);
      const blob = await downloadAttachment(attachment.id);
      
      // 创建下载链接
      const url = window.URL.createObjectURL(new Blob([blob]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', attachment.name);
      document.body.appendChild(link);
      link.click();
      
      // 清理
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      toast.success(`${attachment.name} 下载成功`);
    } catch (error) {
      toast.error(`下载失败: ${error.message}`);
    }
  };

  const handleAddComment = async () => {
    if (!newComment.trim()) return;
    
    try {
      const formData = new FormData();
      formData.append('content', newComment);
      
      const response = await addComment(article.id, formData);
      
      if (response.success) {
        setNewComment('');
        loadComments();
        toast.success('评论发表成功');
      }
    } catch (error) {
      toast.error(`评论发表失败: ${error.message}`);
    }
  };

  const handleAddReply = async (parentId) => {
    if (!replyContent.trim()) return;
    
    try {
      const formData = new FormData();
      formData.append('content', replyContent);
      formData.append('parentId', parentId);
      
      const response = await addComment(article.id, formData);
      
      if (response.success) {
        setReplyContent('');
        setReplyingTo(null);
        loadComments();
        toast.success('回复发表成功');
      }
    } catch (error) {
      toast.error(`回复发表失败: ${error.message}`);
    }
  };

  const loadComments = async () => {
    try {
      const response = await getComments(article.id);
      if (response.success) {
        setComments(response.data);
      }
    } catch (error) {
      toast.error(`加载评论失败: ${error.message}`);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  };

  const renderComments = () => {
    return comments.map(comment => (
      <div key={comment.id} className="mb-4">
        <div className="bg-gray-50 p-4 rounded-lg">
          <div className="flex justify-between items-start mb-2">
            <span className="font-medium text-blue-600">{comment.nickname}</span>
            <span className="text-sm text-gray-500">{formatDate(comment.createTime)}</span>
          </div>
          <p className="text-gray-800 mb-3">{comment.content}</p>
          <Button 
            variant="outline" 
            size="sm" 
            onClick={() => setReplyingTo(comment.id)}
          >
            <MessageCircle className="w-4 h-4 mr-1" />
            回复
          </Button>
        </div>
        
        {comment.replies && comment.replies.map(reply => (
          <div key={reply.id} className="ml-8 mt-3 bg-gray-100 p-3 rounded-lg">
            <div className="flex justify-between items-start mb-2">
              <span className="font-medium text-green-600">{reply.nickname}</span>
              <span className="text-sm text-gray-500">{formatDate(reply.createTime)}</span>
            </div>
            <p className="text-gray-800">{reply.content}</p>
          </div>
        ))}
        
        {replyingTo === comment.id && (
          <div className="ml-8 mt-3 flex">
            <input
              type="text"
              value={replyContent}
              onChange={(e) => setReplyContent(e.target.value)}
              placeholder="输入回复内容..."
              className="flex-1 border rounded-l-lg px-3 py-2"
            />
            <Button 
              onClick={() => handleAddReply(comment.id)}
              className="rounded-l-none"
            >
              发表
            </Button>
            <Button 
              variant="outline" 
              onClick={() => setReplyingTo(null)}
            >
              取消
            </Button>
          </div>
        )}
      </div>
    ));
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
      
      {/* 评论区域 */}
      <Card className="mt-8">
        <CardContent className="pt-6">
          <h3 className="text-xl font-semibold mb-4">评论</h3>
          
          {/* 发表评论 */}
          <div className="mb-6">
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="发表你的评论..."
              className="w-full border rounded-lg p-3 mb-2"
              rows="3"
            />
            <div className="flex justify-end">
              <Button onClick={handleAddComment}>发表评论</Button>
            </div>
          </div>
          
          {/* 评论列表 */}
          <div>
            {comments.length > 0 ? (
              renderComments()
            ) : (
              <p className="text-gray-500 text-center py-4">暂无评论，快来发表第一条评论吧！</p>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default ArticleDetail;