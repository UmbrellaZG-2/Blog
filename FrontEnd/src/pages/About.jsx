import React from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ArrowLeft, Github, Mail } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const About = () => {
  const navigate = useNavigate();

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      <Button 
        variant="outline" 
        className="mb-6 flex items-center"
        onClick={() => navigate('/')}
      >
        <ArrowLeft className="w-4 h-4 mr-2" />
        返回首页
      </Button>

      <div className="space-y-8">
        <div className="text-center">
          <img 
            src="/pic/zg.jpg" 
            alt="个人头像" 
            className="mx-auto object-cover w-32 h-32 rounded-full mb-4"
          />
          <h1 className="text-3xl font-bold mb-2">关于我</h1>
          <p className="text-xl text-gray-600">后端开发 & 算法开发 & 电子游戏爱好者</p>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>个人简介</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-gray-700 leading-relaxed">
              我是一名热爱技术的后端开发者，专注于后端开发与超图学习算法的研究实践。在这个博客中，我将分享开发中遇到的问题、解决方案及技术思考，欢迎大家交流探讨。同时，我也会记录一些休闲娱乐爱好，期待与同好分享交流。
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>技术栈</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
              <div className="text-center p-4 bg-blue-50 rounded-lg">
                <h3 className="font-semibold text-blue-800">算法方向</h3>
                <p className="text-sm text-blue-600 mt-2">Hypergraph, Fuzzy Logic, Contrastive learning</p>
              </div>
              <div className="text-center p-4 bg-green-50 rounded-lg">
                <h3 className="font-semibold text-green-800">后端</h3>
                <p className="text-sm text-green-600 mt-2">Python, Java</p>
              </div>
              <div className="text-center p-4 bg-purple-50 rounded-lg">
                <h3 className="font-semibold text-purple-800">数据库</h3>
                <p className="text-sm text-purple-600 mt-2">MySQL, MongoDB, Redis</p>
              </div>
              <div className="text-center p-4 bg-orange-50 rounded-lg">
                <h3 className="font-semibold text-orange-800">机器学习</h3>
                <p className="text-sm text-orange-600 mt-2">pytroch, tensorflow, matlab</p>
              </div>
              <div className="text-center p-4 bg-red-50 rounded-lg">
                <h3 className="font-semibold text-red-800">工具</h3>
                <p className="text-sm text-red-600 mt-2">Git, VS Code, LLM</p>
              </div>
              <div className="text-center p-4 bg-indigo-50 rounded-lg">
                <h3 className="font-semibold text-indigo-800">前端</h3>
                <p className="text-sm text-indigo-600 mt-2">React</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>联系方式</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex flex-col gap-4">
              <Button 
                variant="outline" 
                className="flex items-center justify-start"
                onClick={() => window.open('mailto:2259936975@qq.com')}
              >
                <Mail className="w-4 h-4 mr-2" />
                邮箱: 2259936975@qq.com
              </Button>
              <Button 
                variant="outline" 
                className="flex items-center justify-start"
                onClick={() => window.open('https://github.com/UmbrellaZG-2', '_blank')}
              >
                <Github className="w-4 h-4 mr-2" />
                GitHub: UmbrellaZG-2
              </Button>
              <Button 
                variant="outline" 
                className="flex items-center justify-start"
                onClick={() => window.open('https://blog.csdn.net/m0_51576139', '_blank')}
              >
                <svg className="w-4 h-4 mr-2" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 0c-6.627 0-12 5.373-12 12s5.373 12 12 12 12-5.373 12-12-5.373-12-12-12zm-1.5 16.5h-3v-9h3v9zm-1.5-10.5c-.966 0-1.75-.784-1.75-1.75s.784-1.75 1.75-1.75 1.75.784 1.75 1.75-.784 1.75-1.75 1.75zm13.5 10.5h-3v-4.5c0-1.103-.897-2-2-2s-2 .897-2 2v4.5h-3v-9h3v1.102c.539-.729 1.389-1.102 2.5-1.102 1.862 0 3 1.343 3 3v5.5z"/>
                </svg>
                CSDN: umbrellazg
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default About;
