import React from 'react';
import { Button } from '@/components/ui/button';

const CategoryNav = ({ categories = [], activeCategory, onCategoryChange }) => {
  // 确保categories是数组类型
  const safeCategories = Array.isArray(categories) ? categories : [];

  return (
    <div className="flex flex-wrap gap-2 mb-8 justify-center">
      {safeCategories.map((category, index) => (
        <Button
          key={typeof category === 'object' ? category.id : index}
          variant={activeCategory === (typeof category === 'object' ? category.id : category) ? 'default' : 'outline'}
          onClick={() => onCategoryChange(typeof category === 'object' ? category.id : category)}
        >
          {typeof category === 'object' ? category.name : category}
        </Button>
      ))}
    </div>
  );
};

export default CategoryNav;
