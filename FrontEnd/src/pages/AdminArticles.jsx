import React, { useState, useEffect } from 'react';
import { deleteArticleTag, addArticleTag, deleteArticle, getArticles } from '@/services/api';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Plus, Edit, Trash2, Search } from 'lucide-react';
import { toast } from 'sonner';

const AdminArticles = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 获取文章列表
  useEffect(() => {
    const fetchArticles = async () => {
      try {
        setLoading(true);
        const data = await getArticles();
        setArticles(data);
      } catch (err) {
        setError('获取文章列表失败');
        console.error('Failed to fetch articles:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchArticles();
  }, []);

  // 过滤文章
  const filteredArticles = articles.filter(article => 
    article?.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    article?.category?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    article?.tags?.some(tag => tag.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const handleEdit = (id) => {
    // 实际应用中会跳转到编辑页面
    toast.info(`编辑文章 ID: ${id}`);
  };

  const handleDelete = async (id) => {
    try {
      await deleteArticle(id);
      // 从列表中移除删除的文章
      setArticles(prev => prev.filter(article => article.id !== id));
      toast.success(`已删除文章 ID: ${id}`);
    } catch (error) {
      toast.error(`删除文章失败: ${error.response?.data?.message || error.message}`);
      console.error('Failed to delete article:', error);
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <Button 
          variant="outline" 
          className="flex items-center"
          onClick={() => navigate('/')}
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          返回首页
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
                          {article.tags.map((tag, index) => (
                            <span 
                              key={index} 
                              className="relative group bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded cursor-pointer"
                            >
                              {tag}
                              <Button 
                                variant="destructive" 
                                size="sm" 
                                className="absolute -top-6 left-1/2 transform -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap"
                                onClick={async () => {
                                  try {
                                    await deleteArticleTag(article.id, tag.id);
                                    // 更新文章标签列表
                                    const updatedArticles = articles.map(a => {
                                      if (a.id === article.id) {
                                        return { ...a, tags: a.tags.filter(t => t.id !== tag.id) };
                                      }
                                      return a;
                                    });
                                    setArticles(updatedArticles);
                                    toast.success(`已删除标签: ${tag.name}`);
                                  } catch (error) {
                                    toast.error(`删除标签失败: ${error.response?.data?.message || error.message}`);
                                    console.error('Failed to delete tag:', error);
                                  }
                                }}
                              >
                                删除
                              </Button>
                            </span>
                          ))}
                          <Button 
                            variant="outline" 
                            size="sm" 
                            className="h-5 px-1 text-xs" 
                            onClick={async () => {
                              try {
                                const newTagId = prompt('请输入标签ID:');
                                if (newTagId) {
                                  await addArticleTag(article.id, newTagId);
                                  // 刷新文章列表
                                  const data = await getArticles();
                                  setArticles(data);
                                  toast.success('标签添加成功');
                                }
                              } catch (error) {
                                toast.error(`添加标签失败: ${error.response?.data?.message || error.message}`);
                                console.error('Failed to add tag:', error);
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
                      <td className="py-3 px-4">{article.createdAt}</td>
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
