import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { ArrowLeft, Save, Eye, Upload, X } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { createArticle, uploadAttachment, uploadCoverImage } from '@/services/api';

const NewArticle = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: '',
    summary: '',
    content: '',
    category: '',
    tags: '',
    status: 'draft'
  });
  const [attachments, setAttachments] = useState([]);
  const [coverImage, setCoverImage] = useState(null);
  const [coverImagePreview, setCoverImagePreview] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const categories = [
    { id: 'tech', name: '技术' },
    { id: 'game', name: '游戏' }
  ];

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
      
      // 生成预览URL
      const previewUrl = URL.createObjectURL(file);
      setCoverImagePreview(previewUrl);
    }
  };

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    const newAttachments = files.map(file => ({
      id: Date.now() + Math.random(),
      file,
      name: file.name,
      size: `${(file.size / 1024).toFixed(2)} KB`,
      type: file.type
    }));
    
    setAttachments(prev => [...prev, ...newAttachments]);
  };

  const removeAttachment = (id) => {
    setAttachments(prev => prev.filter(attachment => attachment.id !== id));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    try {
      // 基本验证
      if (!formData.title.trim()) {
        toast.error('请输入文章标题');
        setIsSubmitting(false);
        return;
      }
      
      if (!formData.content.trim()) {
        toast.error('请输入文章内容');
        setIsSubmitting(false);
        return;
      }

      // 准备文章数据
      const articleData = {
        title: formData.title,
        summary: formData.summary,
        content: formData.content,
        category: formData.category,
        tags: formData.tags ? formData.tags.split(',').map(tag => tag.trim()) : [],
        status: formData.status
      };

      // 创建文章
      const response = await createArticle(articleData);
      
      if (response.success) {
        const articleId = response.data.id;
        
        // 上传封面图片（如果有）
        if (coverImage) {
          try {
            await uploadCoverImage(articleId, coverImage);
          } catch (error) {
            console.error('封面图片上传失败:', error);
            toast.warning('封面图片上传失败，但文章已保存');
          }
        }
        
        // 上传附件（如果有）
        if (attachments.length > 0) {
          try {
            for (const attachment of attachments) {
              const formData = new FormData();
              formData.append('file', attachment.file);
              formData.append('articleId', articleId);
              await uploadAttachment(formData);
            }
          } catch (error) {
            console.error('附件上传失败:', error);
            toast.warning('部分附件上传失败，但文章已保存');
          }
        }
        
        toast.success('文章保存成功！');
        navigate('/admin/articles');
      } else {
        toast.error(`文章保存失败：${response.message}`);
      }
    } catch (error) {
      toast.error(`文章保存失败：${error.message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handlePreview = () => {
    if (!formData.title.trim() || !formData.content.trim()) {
      toast.error('请先填写标题和内容');
      return;
    }
    
    // 模拟预览功能
    toast.info('预览功能开发中...');
  };

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      <div className="flex items-center justify-between mb-6">
        <Button 
          variant="outline" 
          className="flex items-center"
          onClick={() => navigate('/admin/articles')}
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          返回文章管理
        </Button>
        <h1 className="text-2xl font-bold">新建文章</h1>
        <div className="flex gap-2">
          <Button variant="outline" onClick={handlePreview} disabled={isSubmitting}>
            <Eye className="w-4 h-4 mr-2" />
            预览
          </Button>
          <Button onClick={handleSubmit} disabled={isSubmitting}>
            {isSubmitting ? '保存中...' : (
              <>
                <Save className="w-4 h-4 mr-2" />
                保存
              </>
            )}
          </Button>
        </div>
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
                  placeholder="请输入标签，用逗号分隔"
                  value={formData.tags}
                  onChange={(e) => handleInputChange('tags', e.target.value)}
                />
                <p className="text-sm text-gray-500 mt-1">多个标签请用逗号分隔，例如：React,JavaScript</p>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">封面图片（可选）</label>
              <div className="flex items-center gap-2">
                <Input
                  type="file"
                  accept="image/*"
                  onChange={handleCoverImageChange}
                  className="flex-1"
                />
                <Button type="button" variant="outline">
                  <Upload className="w-4 h-4 mr-2" />
                  选择图片
                </Button>
              </div>
              {coverImagePreview && (
                <div className="mt-2">
                  <img 
                    src={coverImagePreview} 
                    alt="封面预览" 
                    className="mx-auto object-cover w-full h-48 rounded-lg"
                  />
                </div>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">发布状态</label>
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
                保存文章
              </>
            )}
          </Button>
        </div>
      </form>
    </div>
  );
};

export default NewArticle;
