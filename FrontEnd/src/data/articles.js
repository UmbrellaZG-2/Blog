// 模拟文章数据
export const mockArticles = [
  {
    id: 1,
    title: "React Hooks 完全指南",
    summary: "深入理解React Hooks的工作原理和最佳实践，包括useState、useEffect、useContext等常用Hooks的使用方法。",
    content: `
      <h2>什么是React Hooks？</h2>
      <p>React Hooks是React 16.8版本引入的新特性，它允许你在不编写class的情况下使用state以及其他的React特性。</p>
      
      <h2>常用的Hooks</h2>
      <h3>useState</h3>
      <p>useState是React Hooks中最常用的Hook之一，它允许你在函数组件中添加state。</p>
      
      <h3>useEffect</h3>
      <p>useEffect Hook允许你在函数组件中执行副作用操作，例如数据获取、订阅或手动更改DOM。</p>
      
      <h3>useContext</h3>
      <p>useContext Hook允许你在函数组件中订阅React context，而不需要使用Consumer组件。</p>
      
      <h2>最佳实践</h2>
      <p>1. 只在函数组件的顶层调用Hooks<br>
         2. 只在React函数组件中调用Hooks<br>
         3. 使用自定义Hooks来复用组件间的逻辑</p>
    `,
    category: "前端开发",
    tags: [
      { id: 1, name: "React" },
      { id: 2, name: "JavaScript" },
      { id: 3, name: "前端" }
    ],
    author: "UmbrellaZG",
    createdAt: "2023-06-15T10:00:00Z",
    updatedAt: "2023-06-15T10:00:00Z",
    readTime: 8,
    coverImage: "https://nocode.meituan.com/photo/search?keyword=react,code&width=800&height=400",
    attachments: [
      {
        id: 1,
        name: "react-hooks-cheatsheet.pdf",
        size: "2.4 MB",
        type: "application/pdf",
        downloadCount: 128
      },
      {
        id: 2,
        name: "example-code.zip",
        size: "1.1 MB",
        type: "application/zip",
        downloadCount: 96
      }
    ]
  },
  {
    id: 2,
    title: "Python数据分析实战",
    summary: "使用Python进行数据分析的完整指南，涵盖pandas、numpy、matplotlib等核心库的使用方法和实际案例。",
    content: `
      <h2>Python数据分析生态系统</h2>
      <p>Python拥有丰富的数据分析库，构成了强大的数据分析生态系统。</p>
      
      <h2>核心库介绍</h2>
      <h3>pandas</h3>
      <p>pandas是Python中最重要的数据分析库，提供了高性能、易用的数据结构和数据分析工具。</p>
      
      <h3>numpy</h3>
      <p>numpy是Python科学计算的基础库，提供了多维数组对象和各种派生对象。</p>
      
      <h3>matplotlib</h3>
      <p>matplotlib是Python中最流行的绘图库，可以生成各种静态、动态和交互式图表。</p>
      
      <h2>实际案例</h2>
      <p>我们将通过一个销售数据分析的案例来演示如何使用这些库进行实际的数据分析工作。</p>
    `,
    category: "数据科学",
    tags: [
      { id: 4, name: "Python" },
      { id: 5, name: "数据分析" },
      { id: 6, name: "机器学习" }
    ],
    author: "UmbrellaZG",
    createdAt: "2023-06-10T14:30:00Z",
    updatedAt: "2023-06-12T09:15:00Z",
    readTime: 12,
    coverImage: "https://nocode.meituan.com/photo/search?keyword=python,data,analysis&width=800&height=400",
    attachments: [
      {
        id: 3,
        name: "sales-data-2023.csv",
        size: "512 KB",
        type: "text/csv",
        downloadCount: 256
      }
    ]
  }
];
