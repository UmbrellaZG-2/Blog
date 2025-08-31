import { HomeIcon, FileText, LogIn, User, FileEdit, Plus, MessageCircle } from "lucide-react";
import Index from "./pages/Index.jsx";
import Article from "./pages/Article.jsx";
import Login from "./pages/Login.jsx";
import About from "./pages/About.jsx";
import AdminArticles from "./pages/AdminArticles.jsx";
import NewArticle from "./pages/NewArticle.jsx";
import AdminComments from "./pages/AdminComments.jsx";

/**
* Central place for defining the navigation items. Used for navigation components and routing.
*/
export const navItems = [
  {
    title: "首页",
    to: "/",
    icon: <HomeIcon className="h-4 w-4" />,
    page: <Index />,
  },
  {
    title: "文章",
    to: "/article/:id",
    icon: <FileText className="h-4 w-4" />,
    page: <Article />,
  },
  {
    title: "登录",
    to: "/login",
    icon: <LogIn className="h-4 w-4" />,
    page: <Login />,
  },
  {
    title: "关于我",
    to: "/about",
    icon: <User className="h-4 w-4" />,
    page: <About />,
  },
  {
    title: "文章管理",
    to: "/admin/articles",
    icon: <FileEdit className="h-4 w-4" />,
    page: <AdminArticles />,
  },
  {
    title: "新建文章",
    to: "/admin/articles/new",
    icon: <Plus className="h-4 w-4" />,
    page: <NewArticle />,
  },
  {
    title: "评论管理",
    to: "/admin/comments",
    icon: <MessageCircle className="h-4 w-4" />,
    page: <AdminComments />,
  },
];
