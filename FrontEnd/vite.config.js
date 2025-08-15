import { fileURLToPath, URL } from 'url';
import { defineConfig, loadEnv } from 'vite';
import { resolve } from 'path';
import { tmpdir } from 'os';
// Removed nocode plugins
import react from '@vitejs/plugin-react';

const CHAT_VARIABLE = process.env.CHAT_VARIABLE || '';
const PUBLIC_PATH = process.env.PUBLIC_PATH || '';

const isProdEnv = process.env.NODE_ENV === 'production';
const publicPath = (isProdEnv && CHAT_VARIABLE)
  ? PUBLIC_PATH + '/' + CHAT_VARIABLE
  : PUBLIC_PATH + '/';
const outDir = (isProdEnv && CHAT_VARIABLE) ? 'build/' + CHAT_VARIABLE : 'build';
const plugins = isProdEnv
  ? CHAT_VARIABLE
    ? [react(), prodHtmlTransformer(CHAT_VARIABLE)]
    : [react()]
  : [
      // devLogger({
      //   dirname: resolve(tmpdir(), '.umbrella-dev-logs'),
      //   maxFiles: '3d',
      // }),
      react(),
      // devHtmlTransformer(CHAT_VARIABLE),
    ];

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd(), 'VITE_');
  const apiBaseUrl = env.VITE_API_BASE_URL || 'http://localhost:8081/api';
  const proxyTarget = apiBaseUrl.replace(/\/api$/, '');

  // 确保proxyTarget有值
  if (!proxyTarget) {
    throw new Error('VITE_API_BASE_URL is not defined or invalid in environment variables');
  }

  return {
    server: {
      host: '::',
      port: '8080',
      hmr: {
        overlay: false,
      },
      proxy: {
        '/api': {
          target: proxyTarget,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    },
    plugins,
    base: publicPath,
    build: {
      outDir,
    },
    resolve: {
      alias: [
        {
          find: '@',
          replacement: fileURLToPath(new URL('./src', import.meta.url)),
        },
        {
          find: 'lib',
          replacement: resolve(__dirname, 'lib'),
        },
      ],
    },
  }
 });
