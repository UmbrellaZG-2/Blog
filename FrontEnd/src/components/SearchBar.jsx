import React, { useState } from 'react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Search } from 'lucide-react';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

const SearchBar = ({ onSearch }) => {
  const [keyword, setKeyword] = useState('');
  const [searchType, setSearchType] = useState('title'); // 默认按文章名搜索

  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch({ keyword, type: searchType });
  };

  return (
    <form onSubmit={handleSubmit} className="flex gap-2 mb-8">
      <Select value={searchType} onValueChange={setSearchType}>
        <SelectTrigger className="w-[120px]">
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="title">按文章名</SelectItem>
          <SelectItem value="tag">按标签</SelectItem>
        </SelectContent>
      </Select>
      <Input
        type="text"
        placeholder={
          searchType === 'title' ? '搜索文章...' : '搜索标签...'
        }
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        className="flex-1"
      />
      <Button type="submit">
        <Search className="w-4 h-4 mr-2" />
        搜索
      </Button>
    </form>
  );
};

export default SearchBar;
