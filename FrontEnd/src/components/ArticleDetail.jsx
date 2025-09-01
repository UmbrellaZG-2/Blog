import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getArticle, likeArticle, getComments, addComment, addReply, downloadAttachment } from '@/services/api';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Calendar, Tag, ThumbsUp, MessageCircle, ArrowLeft, Download } from 'lucide-react';

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

  // 确保tags和attachments是数组类型
  const safeTags = Array.isArray(article?.tags) ? article.tags : [];
  const safeAttachments = Array.isArray(article?.attachments) ? article.attachments : [];

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
    if (article) {
      setIsLiked(article.liked || false);
      setLikes(article.likes || 0);
    }
  }, [article]);

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
      const response = await downloadAttachment(attachment.id);
      const blob = new Blob([response.data], { type: response.headers['content-type'] });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = attachment.name;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('下载失败:', error);
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
            {article.createTime ? formatDate(article.createTime) : '未知时间'}
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
        
        {(article.coverImage && article.coverImage !== 'false') ? (
          <img 
            src={article.coverImage} 
            alt={article.title} 
            className="w-full h-96 object-cover rounded-lg mb-8"
          />
        ) : (
          <img 
            src="/resource/pic/cover.png" 
            alt={article.title} 
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
                    <div key={attachment.id || attachment.name} className="flex items-center justify-between p-3 border rounded-lg">
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