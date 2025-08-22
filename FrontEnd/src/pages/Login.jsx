import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { User, Lock } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { login, guestLogin } from '@/services/api';

const Login = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      // 调用登录API
      const response = await login({ username, password });
      toast.success('登录成功！');
      navigate('/admin/articles');
    } catch (error) {
      // 登录失败，显示错误提示
      toast.error('登录失败：用户名或密码错误');
      console.error('Login error:', error);
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
            <Button type="submit" className="w-full">
              登录
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
              onClick={async () => {
                try {
                  await guestLogin();
                  toast.success('游客登录成功');
                  navigate('/');
                } catch (error) {
                  toast.error(`游客登录失败: ${error.response?.data?.message || error.message}`);
                  console.error('Guest login failed:', error);
                }
              }}
            >
              以游客身份访问
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default Login;
