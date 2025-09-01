import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { getArticleById, updateArticle } from '@/services/api';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { ArrowLeft, Save, Upload, X } from 'lucide-react';
import { toast } from 'sonner';

const EditArticle = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const queryClient = useQueryClient();
  const [isAuthorized, setIsAuthorized] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    summary: '',
    content: '',
    category: '',
    tags: '',
    status: 'draft'
  });
  const [coverImage, setCoverImage] = useState(null);
  const [coverImagePreview, setCoverImagePreview] = useState(null);
  const [attachments, setAttachments] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 权限验证
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
    } else {
      setIsAuthorized(true);
    }
  }, [navigate]);

  // 获取文章详情
  const { data: article, isLoading, error } = useQuery({
    queryKey: ['article', id],
    queryFn: () => getArticleById(id),
    enabled: isAuthorized && !!id,
  });

  // 填充表单数据
  useEffect(() => {
    if (article?.data) {
      const articleData = article.data;
      setFormData({
        title: articleData.title || '',
        summary: articleData.summary || '',
        content: articleData.content || '',
        category: articleData.category || '',
        tags: Array.isArray(articleData.tags) ? articleData.tags.join(',') : '',
        status: articleData.status || 'draft'
      });
      
      if (articleData.coverImage && articleData.coverImage !== 'false') {
        setCoverImagePreview(articleData.coverImage);
      }
    }
  }, [article]);

  // 固定分类选项
  const categories = [
    { id: 'technology', name: '技术' },
    { id: 'entertainment', name: '娱乐' },
    { id: 'other', name: '其他' }
  ];

  if (!isAuthorized) {
    return <div className="container mx-auto px-4 py-8">加载中...</div>;
  }

  if (isLoading) {
    return <div className="container mx-auto px-4 py-8">加载中...</div>;
  }

  if (error) {
    return <div className="container mx-auto px-4 py-8 text-red-500">加载失败: {error.message}</div>;
  }

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleCoverImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setCoverImage(file);
      const reader = new FileReader();
      reader.onload = (e) => {
        setCoverImagePreview(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    const newAttachments = files.map(file => ({
      id: Math.random().toString(36).substr(2, 9),
      name: file.name,
      size: (file.size / 1024 / 1024).toFixed(2) + 'MB',
      file: file
    }));
    setAttachments(prev => [...prev, ...newAttachments]);
  };

  const removeAttachment = (attachmentId) => {
    setAttachments(prev => prev.filter(att => att.id !== attachmentId));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      const formDataToSend = new FormData();
      formDataToSend.append('title', formData.title);
      formDataToSend.append('summary', formData.summary);
      formDataToSend.append('content', formData.content);
      formDataToSend.append('category', formData.category);
      formDataToSend.append('tags', formData.tags);
      formDataToSend.append('status', formData.status);

      if (coverImage) {
        formDataToSend.append('coverImage', coverImage);
      }

      attachments.forEach((attachment, index) => {
        formDataToSend.append(`attachments`, attachment.file);
      });

      const response = await updateArticle(id, formDataToSend);
      
      if (response.success) {
        queryClient.invalidateQueries(['adminArticles']);
        toast.success('文章更新成功');
        navigate('/admin/articles');
      } else {
        toast.error(`更新失败：${response.message}`);
      }
    } catch (error) {
      toast.error(`更新失败：${error.message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <Button 
          variant="outline" 
          className="flex items-center"
          onClick={() => navigate('/admin/articles')}
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          返回文章管理
        </Button>
        <h1 className="text-2xl font-bold">编辑文章</h1>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <Card>
          <CardHeader>
            <CardTitle>基本信息</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <label className="block text-sm font-medium mb-2">文章标题 *</label>
              <Input
                type="text"
                placeholder="请输入文章标题"
                value={formData.title}
                onChange={(e) => handleInputChange('title', e.target.value)}
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">文章摘要</label>
              <Textarea
                placeholder="请输入文章摘要（可选）"
                value={formData.summary}
                onChange={(e) => handleInputChange('summary', e.target.value)}
                rows={3}
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-2">分类 *</label>
                <Select value={formData.category} onValueChange={(value) => handleInputChange('category', value)} required>
                  <SelectTrigger>
                    <SelectValue placeholder="选择分类" />
                  </SelectTrigger>
                  <SelectContent>
                    {categories.map((category) => (
                      <SelectItem key={category.id} value={category.id}>
                        {category.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">标签（可选）</label>
                <Input
                  type="text"
                  placeholder="多个标签用逗号分隔"
                  value={formData.tags}
                  onChange={(e) => handleInputChange('tags', e.target.value)}
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">封面图片（可选）</label>
              <div className="flex items-center gap-4">
                {coverImagePreview && (
                  <img 
                    src={coverImagePreview} 
                    alt="封面预览" 
                    className="w-32 h-20 object-cover rounded-lg"
                  />
                )}
                <Input
                  type="file"
                  accept="image/*"
                  onChange={handleCoverImageChange}
                  className="flex-1"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">状态</label>
              <Select value={formData.status} onValueChange={(value) => handleInputChange('status', value)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="draft">草稿</SelectItem>
                  <SelectItem value="published">发布</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>附件上传（可选）</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-2">
                  选择附件文件
                </label>
                <div className="flex items-center gap-2">
                  <Input
                    type="file"
                    multiple
                    onChange={handleFileChange}
                    className="flex-1"
                  />
                  <Button type="button" variant="outline">
                    <Upload className="w-4 h-4 mr-2" />
                    选择文件
                  </Button>
                </div>
                <p className="text-sm text-gray-500 mt-1">
                  支持上传多个文件，单个文件大小不超过10MB
                </p>
              </div>

              {attachments.length > 0 && (
                <div>
                  <h4 className="font-medium mb-2">已选择的附件:</h4>
                  <div className="space-y-2">
                    {attachments.map((attachment) => (
                      <div 
                        key={attachment.id} 
                        className="flex items-center justify-between p-3 border rounded-lg"
                      >
                        <div>
                          <p className="font-medium">{attachment.name}</p>
                          <p className="text-sm text-gray-500">{attachment.size}</p>
                        </div>
                        <Button
                          type="button"
                          variant="ghost"
                          size="sm"
                          onClick={() => removeAttachment(attachment.id)}
                        >
                          <X className="w-4 h-4" />
                        </Button>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>文章内容 *</CardTitle>
          </CardHeader>
          <CardContent>
            <Textarea
              placeholder="请输入文章内容（支持HTML格式）"
              value={formData.content}
              onChange={(e) => handleInputChange('content', e.target.value)}
              rows={20}
              className="font-mono"
              required
            />
            <p className="text-sm text-gray-500 mt-2">
              提示：您可以使用HTML标签来格式化文章内容，如 &lt;h2&gt;、&lt;p&gt;、&lt;strong&gt; 等
            </p>
          </CardContent>
        </Card>

        <div className="flex justify-end gap-4">
          <Button 
            type="button" 
            variant="outline" 
            onClick={() => navigate('/admin/articles')}
            disabled={isSubmitting}
          >
            取消
          </Button>
          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting ? '保存中...' : (
              <>
                <Save className="w-4 h-4 mr-2" />
                保存修改
              </>
            )}
          </Button>
        </div>
      </form>
    </div>
  );
};

export default EditArticle;