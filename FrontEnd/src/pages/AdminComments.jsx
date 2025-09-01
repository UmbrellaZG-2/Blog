import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Search, Edit, Trash2, MessageCircle } from 'lucide-react';
import { toast } from 'sonner';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { getAllComments, updateComment, deleteComment } from '@/services/api';

const AdminComments = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [searchTerm, setSearchTerm] = useState('');
  const [editingComment, setEditingComment] = useState(null);
  const [editContent, setEditContent] = useState('');
  const [isAuthorized, setIsAuthorized] = useState(false);

  // 权限验证
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      toast.error('请先登录');
      navigate('/login');
      return;
    }
    setIsAuthorized(true);
  }, [navigate]);

  // 获取评论列表
  const { data: comments, isLoading, error } = useQuery({
    queryKey: ['adminComments'],
    queryFn: getAllComments,
    enabled: isAuthorized
  });

  if (!isAuthorized) {
    return <div className="container mx-auto px-4 py-8">权限验证中...</div>;
  }

  // 过滤评论
  const filteredComments = (comments?.data || []).filter(comment => 
    comment?.content?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    comment?.nickname?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    comment?.articleTitle?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleEditStart = (comment) => {
    setEditingComment(comment.id);
    setEditContent(comment.content);
  };

  const handleEditCancel = () => {
    setEditingComment(null);
    setEditContent('');
  };

  const handleEditSave = async (commentId) => {
    try {
      const response = await updateComment(commentId, { content: editContent });
      if (response.success) {
        queryClient.invalidateQueries(['adminComments']);
        setEditingComment(null);
        setEditContent('');
        toast.success('评论更新成功');
      } else {
        toast.error(`更新失败：${response.message}`);
      }
    } catch (error) {
      toast.error(`更新失败：${error.message}`);
    }
  };

  const handleDelete = async (commentId) => {
    try {
      const response = await deleteComment(commentId);
      if (response.success) {
        queryClient.invalidateQueries(['adminComments']);
        toast.success('评论删除成功');
      } else {
        toast.error(`删除失败：${response.message}`);
      }
    } catch (error) {
      toast.error(`删除失败：${error.message}`);
    }
  };

  if (isLoading) {
    return <div className="container mx-auto px-4 py-8">加载中...</div>;
  }

  if (error) {
    return <div className="container mx-auto px-4 py-8 text-red-500">加载失败: {error.message}</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <Button 
          variant="outline" 
          className="flex items-center"
          onClick={() => navigate('/admin')}
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          返回管理
        </Button>
        <h1 className="text-2xl font-bold">评论管理</h1>
      </div>

      {/* 搜索框 */}
      <div className="mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
          <Input
            type="text"
            placeholder="搜索评论内容、昵称或文章标题..."
            className="pl-10"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>评论列表</CardTitle>
        </CardHeader>
        <CardContent>
          {filteredComments.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-500">暂无评论数据</p>
            </div>
          ) : (
            <div className="space-y-4">
              {filteredComments.map((comment) => (
                <div key={comment.id} className="border rounded-lg p-4">
                  <div className="flex justify-between items-start mb-2">
                    <div className="flex items-center gap-2">
                      <MessageCircle className="w-4 h-4 text-blue-500" />
                      <span className="font-medium text-blue-600">{comment.nickname}</span>
                      <span className="text-sm text-gray-500">
                        发布于 {new Date(comment.createTime).toLocaleString()}
                      </span>
                    </div>
                    <div className="flex gap-2">
                      <Button 
                        variant="outline" 
                        size="sm" 
                        onClick={() => handleEditStart(comment)}
                      >
                        <Edit className="w-4 h-4" />
                      </Button>
                      <Button 
                        variant="outline" 
                        size="sm"
                        onClick={() => handleDelete(comment.id)}
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                  
                  {editingComment === comment.id ? (
                    <div className="mt-2">
                      <Input
                        value={editContent}
                        onChange={(e) => setEditContent(e.target.value)}
                        className="mb-2"
                      />
                      <div className="flex gap-2">
                        <Button 
                          size="sm" 
                          onClick={() => handleEditSave(comment.id)}
                        >
                          保存
                        </Button>
                        <Button 
                          variant="outline" 
                          size="sm"
                          onClick={handleEditCancel}
                        >
                          取消
                        </Button>
                      </div>
                    </div>
                  ) : (
                    <p className="text-gray-800">{comment.content}</p>
                  )}
                  
                  {comment.articleTitle && (
                    <div className="mt-2 text-sm text-gray-600">
                      所属文章: {comment.articleTitle}
                    </div>
                  )}
                  
                  {comment.parentId && (
                    <div className="mt-1 text-sm text-gray-500">
                      回复评论ID: {comment.parentId}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default AdminComments;