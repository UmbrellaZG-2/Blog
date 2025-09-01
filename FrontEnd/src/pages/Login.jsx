import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { User, Lock } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { adminLogin, guestLogin } from '@/services/api';

const Login = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    
    try {
      // 调用管理员登录API
      const response = await adminLogin({ username, password });
      
      if (response.token) {
        // 保存token到localStorage
        localStorage.setItem('token', response.token);
        toast.success('登录成功！');
        // 使用navigate跳转到管理员文章管理页面
        setTimeout(() => {
          navigate('/admin/articles');
        }, 500);
      } else {
        toast.error(`登录失败：${response.message || '未知错误'}`);
      }
    } catch (error) {
      console.error('登录错误:', error);
      toast.error(`登录失败：${error.message || '网络错误'}`);
    } finally {
      setIsLoading(false);
    }
  };

  const handleGuestLogin = async () => {
    setIsLoading(true);
    
    try {
      // 调用游客登录API
      const response = await guestLogin();
      
      if (response.token) {
        // 保存token到localStorage
        localStorage.setItem('token', response.token);
        toast.info('以游客身份访问');
        // 使用navigate跳转到首页
        setTimeout(() => {
          navigate('/');
        }, 500);
      } else {
        toast.error(`游客登录失败：${response.message || '未知错误'}`);
      }
    } catch (error) {
      console.error('游客登录错误:', error);
      toast.error(`游客登录失败：${error.message || '网络错误'}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-2xl text-center">管理员登录</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <div className="relative">
                <User className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input 
                  type="text" 
                  placeholder="用户名" 
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="pl-10"
                  required
                />
              </div>
            </div>
            <div className="space-y-2">
              <div className="relative">
                <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input 
                  type="password" 
                  placeholder="密码" 
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="pl-10"
                  required
                />
              </div>
            </div>
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? '登录中...' : '登录'}
            </Button>
          </form>
          <div className="mt-4 space-y-3">
            <Button 
              variant="outline" 
              className="w-full"
              onClick={() => navigate('/')}
            >
              返回首页
            </Button>
            <Button 
              variant="outline" 
              className="w-full"
              onClick={handleGuestLogin}
              disabled={isLoading}
            >
              {isLoading ? '登录中...' : '以游客身份访问'}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default Login;