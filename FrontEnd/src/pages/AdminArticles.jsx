import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { getAdminArticles, deleteArticle, addArticleTags, deleteArticleTag } from '@/services/api';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ArrowLeft, Edit, Plus, Search, Trash2 } from 'lucide-react';
import { toast } from 'sonner';

const AdminArticles = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [searchTerm, setSearchTerm] = useState('');
  const [isAuthorized, setIsAuthorized] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
    } else {
      setIsAuthorized(true);
    }
  }, [navigate]);

  // 获取文章列表
  const { data: articles, isLoading, error } = useQuery({
    queryKey: ['adminArticles'],
    queryFn: getAdminArticles,
    enabled: isAuthorized,
  });

  // 过滤文章
  const filteredArticles = (articles?.data?.articles || []).filter(article =>
    article?.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    article?.category?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    article?.tags?.some(tag => tag.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const handleEdit = (id) => {
    // 跳转到编辑页面
    navigate(`/admin/articles/edit/${id}`);
  };

  const handleAddTag = async (articleId, tagName) => {
    try {
      const response = await addArticleTags(articleId, [tagName]);
      if (response.success) {
        queryClient.invalidateQueries(['adminArticles']);
        toast.success('标签添加成功');
      } else {
        toast.error(`添加标签失败：${response.message}`);
      }
    } catch (error) {
      toast.error(`添加标签失败：${error.message}`);
    }
  };

  const handleDeleteTag = async (articleId, tagName) => {
    try {
      const response = await deleteArticleTag(articleId, tagName);
      if (response.success) {
        queryClient.invalidateQueries(['adminArticles']);
        toast.success('标签删除成功');
      } else {
        toast.error(`删除标签失败：${response.message}`);
      }
    } catch (error) {
      toast.error(`删除标签失败：${error.message}`);
    }
  };

  const handleDelete = async (id) => {
    try {
      const response = await deleteArticle(id);
      if (response.success) {
        // 成功删除后刷新数据
        queryClient.invalidateQueries(['adminArticles']);
        toast.success('文章删除成功');
      } else {
        toast.error(`删除失败：${response.message}`);
      }
    } catch (error) {
      toast.error(`删除失败：${error.message}`);
    }
  };

  if (!isAuthorized) {
    return <div className="container mx-auto px-4 py-8">加载中...</div>;
  }

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
          onClick={() => {
            localStorage.removeItem('token');
            navigate('/');
          }}
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          退出登录
        </Button>
        <h1 className="text-2xl font-bold">文章管理</h1>
        <Button onClick={() => navigate('/admin/articles/new')}>
          <Plus className="w-4 h-4 mr-2" />
          新建文章
        </Button>
      </div>

      {/* 搜索框 */}
      <div className="mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
          <Input
            type="text"
            placeholder="搜索文章标题、分类或标签..."
            className="pl-10"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>文章列表</CardTitle>
        </CardHeader>
        <CardContent>
          {filteredArticles.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-500">暂无文章数据</p>
              <Button 
                className="mt-4" 
                onClick={() => navigate('/admin/articles/new')}
              >
                <Plus className="w-4 h-4 mr-2" />
                创建第一篇文章
              </Button>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b">
                    <th className="text-left py-3 px-4">标题</th>
                    <th className="text-left py-3 px-4">分类</th>
                    <th className="text-left py-3 px-4">标签</th>
                    <th className="text-left py-3 px-4">状态</th>
                    <th className="text-left py-3 px-4">创建时间</th>
                    <th className="text-left py-3 px-4">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredArticles.map((article) => (
                    <tr key={article.id} className="border-b hover:bg-gray-50">
                      <td className="py-3 px-4">{article.title}</td>
                      <td className="py-3 px-4">{article.category}</td>
                      <td className="py-3 px-4">
                        <div className="flex flex-wrap gap-1">
                          {(article.tags || []).map((tag, index) => (
                            <span 
                              key={`${article.id}-${tag}-${index}`} 
                              className="relative group bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded cursor-pointer"
                            >
                              {tag}
                              <Button 
                                variant="destructive" 
                                size="sm" 
                                className="absolute -top-6 left-1/2 transform -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap"
                                onClick={() => handleDeleteTag(article.id, tag)}
                              >
                                删除
                              </Button>
                            </span>
                          ))}
                          <Button 
                            variant="outline" 
                            size="sm" 
                            className="h-5 px-1 text-xs"
                            onClick={() => {
                              const tagName = prompt('请输入新标签名称:');
                              if (tagName && tagName.trim()) {
                                handleAddTag(article.id, tagName.trim());
                              }
                            }}
                          >
                            +
                          </Button>
                        </div>
                      </td>
                      <td className="py-3 px-4">
                        <span className={`px-2 py-1 rounded text-xs ${
                          article.status === '已发布' 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-yellow-100 text-yellow-800'
                        }`}>
                          {article.status}
                        </span>
                      </td>
                      <td className="py-3 px-4">{article.createTime ? (() => {
                        const date = new Date(article.createTime);
                        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
                      })() : '未知时间'}</td>
                      <td className="py-3 px-4">
                        <div className="flex gap-2">
                          <Button 
                            variant="outline" 
                            size="sm" 
                            onClick={() => handleEdit(article.id)}
                          >
                            <Edit className="w-4 h-4" />
                          </Button>
                          <Button 
                            variant="outline" 
                            size="sm"
                            onClick={() => handleDelete(article.id)}
                          >
                            <Trash2 className="w-4 h-4" />
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default AdminArticles;