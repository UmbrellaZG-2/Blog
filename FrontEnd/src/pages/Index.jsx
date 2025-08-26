import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getCategories, getTags, searchArticles, getArticles } from '@/services/api';
import ArticleList from '@/components/ArticleList';
import SearchBar from '@/components/SearchBar';
import CategoryNav from '@/components/CategoryNav';
import TagCloud from '@/components/TagCloud';
import { Skeleton } from '@/components/ui/skeleton';
import { Button } from '@/components/ui/button';
import { useNavigate } from 'react-router-dom';
import { LogIn } from 'lucide-react';

const Index = () => {
  const navigate = useNavigate();
  const [activeCategory, setActiveCategory] = useState(null);
  const [searchParams, setSearchParams] = useState(null);

  // 获取所有文章
  const { data: allArticles, isLoading: allArticlesLoading, error: allArticlesError } = useQuery({
    queryKey: ['articles'],
    queryFn: () => getArticles({ page: 0, size: 10 }),
  });

  // 搜索文章
  const { data: searchResults, isLoading: searchLoading, error: searchError } = useQuery({
    queryKey: ['search', searchParams],
    queryFn: () => searchArticles(searchParams?.keyword, 0, 10),
    enabled: !!searchParams?.keyword, // 只有当搜索关键词存在时才执行查询
  });

  // 获取分类
  const { data: categories, isLoading: categoriesLoading, error: categoriesError } = useQuery({
    queryKey: ['categories'],
    queryFn: getCategories,
  });

  // 获取标签
  const { data: tags, isLoading: tagsLoading, error: tagsError } = useQuery({
    queryKey: ['tags'],
    queryFn: getTags,
  });

  const handleSearch = (params) => {
    setSearchParams(params);
  };

  const handleTagClick = (tag) => {
    setSearchParams({ keyword: tag.name, type: 'tag' });
  };

  // 确定显示的文章列表
  const displayArticles = searchParams?.keyword 
    ? (searchResults?.data?.articles || []) 
    : (allArticles?.data?.articles || []);

  const displayArticlesLoading = searchParams?.keyword 
    ? searchLoading 
    : allArticlesLoading;

  const displayArticlesError = searchParams?.keyword 
    ? searchError 
    : allArticlesError;

  return (
    <div className="container mx-auto px-4 py-8">
      {/* 登录按钮移动到右上角 */}
      <div className="absolute top-4 right-4">
        <Button 
          variant="outline" 
          onClick={() => navigate('/login')}
          className="flex items-center gap-2"
        >
          <LogIn className="h-4 w-4" />
          登录
        </Button>
      </div>

      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold mb-4">Blog of UmbrellaZG</h1>
        <p className="text-xl text-gray-600">分享技术与生活</p>
        <div className="flex justify-center gap-4 mt-6">
          <Button 
            variant="outline" 
            onClick={() => navigate('/about')}
            className="bg-black text-white hover:bg-gray-800"
          >
            关于我
          </Button>
        </div>
      </div>

      <SearchBar onSearch={handleSearch} />

      {searchParams?.keyword && (
        <div className="mb-8">
          <h2 className="text-2xl font-bold mb-4">
            {searchParams.type === 'title' ? '文章搜索结果' : '标签搜索结果'}: "{searchParams.keyword}"
          </h2>
          {displayArticlesLoading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(3)].map((_, i) => (
                <Skeleton key={i} className="h-80 w-full" />
              ))}
            </div>
          ) : displayArticlesError ? (
            <div className="text-center text-red-500">搜索失败: {displayArticlesError.message}</div>
          ) : (
            <ArticleList articles={displayArticles} />
          )}
        </div>
      )}

      {categoriesLoading ? (
        <Skeleton className="h-10 w-full mb-8" />
      ) : categoriesError ? (
        <div className="text-center text-red-500 mb-8">分类加载失败: {categoriesError.message}</div>
      ) : (
        <CategoryNav 
          categories={Array.isArray(categories?.data) ? categories.data : []} 
          activeCategory={activeCategory} 
          onCategoryChange={setActiveCategory} 
        />
      )}

      {tagsLoading ? (
        <Skeleton className="h-20 w-full mb-8" />
      ) : tagsError ? (
        <div className="text-center text-red-500 mb-8">标签加载失败: {tagsError.message}</div>
      ) : (
        <TagCloud 
          tags={Array.isArray(tags?.data) ? tags.data : []} 
          onTagClick={handleTagClick} 
        />
      )}

      {!searchParams?.keyword && (
        <div>
          <h2 className="text-2xl font-bold mb-6 text-center">最新文章</h2>
          {displayArticlesLoading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(6)].map((_, i) => (
                <Skeleton key={i} className="h-80 w-full" />
              ))}
            </div>
          ) : displayArticlesError ? (
            <div className="text-center text-red-500">文章加载失败: {displayArticlesError.message}</div>
          ) : (
            <ArticleList articles={displayArticles} />
          )}
        </div>
      )}
    </div>
  );
};

export default Index;