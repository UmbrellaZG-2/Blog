import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { User, Lock, Mail, Phone } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { userRegister } from '../services/api';

export default function Register() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [email, setEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [isCodeSent, setIsCodeSent] = useState(false);
  const [countdown, setCountdown] = useState(60);

  // 发送验证码
  const sendVerificationCode = async () => {
    if (!username || !password) {
      toast.error('用户名和密码不能为空');
      return;
    }

    if (password !== confirmPassword) {
      toast.error('两次输入的密码不一致');
      return;
    }

    try {
      // 这里假设发送验证码的接口路径
      await fetch('/api/auth/register/send-code', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username,
          password
        }),
      });
      toast.success('验证码已发送，有效期1分钟');
      setIsCodeSent(true);
      startCountdown();
    } catch (error) {
      toast.error(`发送验证码失败: ${error.message}`);
      console.error('Send verification code failed:', error);
    }
  };

  // 开始倒计时
  const startCountdown = () => {
    setCountdown(60);
    const timer = setInterval(() => {
      setCountdown(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          setIsCodeSent(false);
          return 60;
        }
        return prev - 1;
      });
    }, 1000);
  };

  // 提交注册表单
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!username || !password || !confirmPassword || !verificationCode) {
      toast.error('所有字段都不能为空');
      return;
    }

    if (password !== confirmPassword) {
      toast.error('两次输入的密码不一致');
      return;
    }

    try {
      await userRegister({
        username,
        password,
        verificationCode,
        email
      });
      toast.success('注册成功，请登录');
      navigate('/login');
    } catch (error) {
      toast.error(`注册失败: ${error.response?.data || error.message}`);
      console.error('Register failed:', error);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-2xl text-center">用户注册</CardTitle>
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

            <div className="space-y-2">
              <div className="relative">
                <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input 
                  type="password"
                  placeholder="确认密码"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="pl-10"
                  required
                />
              </div>
            </div>

            <div className="space-y-2">
              <div className="relative">
                <Mail className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input 
                  type="email"
                  placeholder="邮箱（选填）"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            <div className="space-y-2 flex">
              <div className="relative flex-1 mr-2">
                <Phone className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input 
                  type="text"
                  placeholder="验证码"
                  value={verificationCode}
                  onChange={(e) => setVerificationCode(e.target.value)}
                  className="pl-10"
                  required
                />
              </div>
              <Button
                variant="outline"
                disabled={isCodeSent}
                onClick={sendVerificationCode}
              >
                {isCodeSent ? `${countdown}秒后重新发送` : '获取验证码'}
              </Button>
            </div>

            <Button type="submit" className="w-full">
              注册
            </Button>
          </form>
          <div className="mt-4 text-center">
            <Button
              variant="text"
              onClick={() => navigate('/login')}
            >
              已有账号？去登录
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}