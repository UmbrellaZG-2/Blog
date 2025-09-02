import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useQueryClient, useMutation } from '@tanstack/react-query';
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { format } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { MessageCircle, Calendar, User, Tag, Paperclip, Download, ArrowLeft, ThumbsUp, Send } from 'lucide-react';
import { getArticle, likeArticle, getComments, addComment, addReply, downloadAttachment } from '@/services/api';
import config from '@/config';

// 根据环境选择正确的API基础URL
const API_BASE_URL = process.env.NODE_ENV === 'production' 
  ? config.production.API_BASE_URL 
  : config.development.API_BASE_URL;

const ArticleDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  
  const [newComment, setNewComment] = useState('');
  const [replyContent, setReplyContent] = useState('');
  const [replyingTo, setReplyingTo] = useState(null);
  const [isLiked, setIsLiked] = useState(false);
  const [likes, setLikes] = useState(0);

  // 获取文章详情
  const { data: articleData, isLoading, error } = useQuery({
    queryKey: ['article', id],
    queryFn: () => getArticle(id),
  });

  // 获取评论
  const { data: commentsData, isLoading: commentsLoading } = useQuery({
    queryKey: ['comments', id],
    queryFn: () => getComments(id),
  });

  const article = articleData?.data;
  const comments = commentsData?.data || [];

  // 映射时间字段，确保前端使用正确的字段名
  const mappedArticle = article ? {
    ...article,
    createTime: article.createdAt || article.createTime,
    updateTime: article.updatedAt || article.updateTime
  } : null;

  // 确保tags和attachments是数组类型
  const safeTags = Array.isArray(mappedArticle?.tags) ? mappedArticle.tags : [];
  const safeAttachments = Array.isArray(mappedArticle?.attachments) ? mappedArticle.attachments : [];

  // 构建封面图片URL
  const getCoverImageUrl = (coverImage) => {
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

  // 构建附件下载URL
  const getAttachmentUrl = (attachment) => {
    if (attachment.filePath && attachment.filePath.startsWith('D:\\')) {
      // 转换本地路径为下载URL
      return `${API_BASE_URL}/attachments/download/${attachment.attachmentId || attachment.id}`;
    }
    return attachment.filePath || `${API_BASE_URL}/attachments/download/${attachment.attachmentId || attachment.id}`;
  };

  // 点赞文章
  const likeMutation = useMutation({
    mutationFn: () => likeArticle(id),
    onSuccess: () => {
      setIsLiked(true);
      setLikes(prev => prev + 1);
    },
    onError: (error) => {
      console.error('点赞失败:', error);
      alert('点赞失败，请稍后重试');
    },
  });

  // 添加评论
  const addCommentMutation = useMutation({
    mutationFn: (content) => addComment(id, { content }),
    onSuccess: () => {
      setNewComment('');
      queryClient.invalidateQueries(['comments', id]);
    },
    onError: (error) => {
      console.error('评论失败:', error);
      alert('评论失败，请稍后重试');
    },
  });

  // 添加回复
  const addReplyMutation = useMutation({
    mutationFn: ({ commentId, content }) => addReply(commentId, content),
    onSuccess: () => {
      setReplyContent('');
      setReplyingTo(null);
      queryClient.invalidateQueries(['comments', id]);
    },
    onError: (error) => {
      console.error('回复失败:', error);
      alert('回复失败，请稍后重试');
    },
  });

  useEffect(() => {
    if (mappedArticle) {
      setIsLiked(mappedArticle.liked || false);
      setLikes(mappedArticle.likes || 0);
    }
  }, [mappedArticle]);

  const handleLike = () => {
    if (!isLiked) {
      likeMutation.mutate();
    }
  };

  const handleAddComment = () => {
    if (newComment.trim()) {
      addCommentMutation.mutate(newComment.trim());
    }
  };

  const handleAddReply = (commentId) => {
    if (replyContent.trim()) {
      addReplyMutation.mutate({ commentId, content: replyContent.trim() });
    }
  };

  const handleDownload = async (attachment) => {
    try {
      // 使用API下载
      const response = await downloadAttachment(attachment.attachmentId || attachment.id);
      
      // 从响应头中获取文件名
      let filename = attachment.fileName || attachment.name;
      
      // 尝试从不同的响应头字段获取文件名
      const headers = response.headers || {};
      
      // 打印所有响应头以调试
      console.log('All response headers:', headers);
      
      // 尝试多种方式获取Content-Disposition头
      let contentDisposition = null;
      if (headers['content-disposition']) {
        contentDisposition = headers['content-disposition'];
      } else if (headers['Content-Disposition']) {
        contentDisposition = headers['Content-Disposition'];
      } else if (headers['Content-disposition']) {
        contentDisposition = headers['Content-disposition'];
      }
      
      console.log('Content-Disposition header:', contentDisposition);
      
      if (contentDisposition) {
        // 尝试匹配 filename*=UTF-8'' 格式（支持中文等特殊字符）
        const utf8FilenameMatch = contentDisposition.match(/filename\*=UTF-8''(.+)$/);
        if (utf8FilenameMatch && utf8FilenameMatch[1]) {
          try {
            filename = decodeURIComponent(utf8FilenameMatch[1]);
            console.log('Using UTF-8 decoded filename:', filename);
          } catch (e) {
            console.warn('Failed to decode UTF-8 filename', e);
          }
        } else {
          // 尝试匹配常规的 filename 格式
          const filenameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
          if (filenameMatch && filenameMatch[1]) {
            filename = filenameMatch[1];
            console.log('Using matched filename:', filename);
          }
        }
      } else {
        // 如果没有Content-Disposition头，尝试从调试头获取文件名
        if (headers['x-debug-original-filename']) {
          filename = headers['x-debug-original-filename'];
          console.log('Using debug filename:', filename);
        }
      }
      
      console.log('Final filename for download:', filename);
      
      // 创建Blob对象
      const contentType = headers['content-type'] || 'application/octet-stream';
      const blob = new Blob([response.data], { type: contentType });
      
      // 创建下载链接
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = filename || (attachment.fileName || attachment.name);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('下载失败:', error);
      // 提供一个默认的文件名以防万一
      const fallbackFilename = attachment.fileName || attachment.name || 'attachment';
      const link = document.createElement('a');
      link.href = `${API_BASE_URL}/attachments/download/${attachment.attachmentId || attachment.id}`;
      link.download = fallbackFilename;
      document.body.appendChild(link);
      link.click();
      link.remove();
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
        <h1 className="text-3xl font-bold mb-4">{mappedArticle.title}</h1>
        
        <div className="flex flex-wrap items-center gap-4 mb-6 text-gray-600">
          <div className="flex items-center">
            <Calendar className="w-4 h-4 mr-1" />
            {mappedArticle.createTime ? formatDate(mappedArticle.createTime) : '未知时间'}
          </div>
          <div className="flex items-center">
            <span className="font-medium">{mappedArticle.author}</span>
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
        
        {getCoverImageUrl(mappedArticle.coverImage) ? (
          <img 
            src={getCoverImageUrl(mappedArticle.coverImage)} 
            alt={mappedArticle.title} 
            className="w-full h-96 object-cover rounded-lg mb-8"
          />
        ) : (
          <img 
            src="/resource/pic/cover.png" 
            alt={mappedArticle.title} 
            className="w-full h-96 object-cover rounded-lg mb-8"
          />
        )}
        
        <div className="mb-8">
          <div className="flex flex-wrap gap-2 mb-6">
            {safeTags.map((tag) => (
              <span key={tag.id || tag.name} className="flex items-center text-sm bg-blue-100 text-blue-800 px-3 py-1 rounded-full">
                <Tag className="w-3 h-3 mr-1" />
                {tag.name}
              </span>
            ))}
          </div>
        </div>
        
        <div 
          className="mb-8"
          dangerouslySetInnerHTML={{ __html: mappedArticle.content }} 
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
                    <div key={attachment.attachmentId || attachment.id || attachment.name} className="flex items-center justify-between p-3 border rounded-lg">
                      <div>
                        <h4 className="font-medium">{attachment.fileName || attachment.name}</h4>
                        <p className="text-sm text-gray-600">
                          {attachment.fileSize || attachment.size} • 
                          下载次数: {attachment.downloadCount || 0}
                        </p>
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
        
        <Separator className="my-8" />
        
        <div className="mb-8">
          <h3 className="text-xl font-semibold mb-4">评论</h3>
          <div className="mb-6">
            <div className="flex items-start space-x-3">
              <Avatar>
                <AvatarFallback>
                  <User className="w-4 h-4" />
                </AvatarFallback>
              </Avatar>
              <div className="flex-1">
                <textarea
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  placeholder="输入评论..."
                  className="w-full border rounded-lg p-3 min-h-[100px]"
                />
                <div className="mt-2 flex justify-end">
                  <Button onClick={handleAddComment}>
                    <Send className="w-4 h-4 mr-2" />
                    发表评论
                  </Button>
                </div>
              </div>
            </div>
          </div>
          
          <div>
            {renderComments()}
          </div>
        </div>
      </article>
    </div>
  );
};

export default ArticleDetail;