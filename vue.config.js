const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    hot: false
    ,proxy: {
      '/api': {  // 匹配所有以/api开头的请求路径
        target: 'http://localhost:8080',  // 代理目标的基础路径
        changeOrigin: true,  // 需要虚拟托管站点
        pathRewrite: {
          '^/api': ''  // 重写路径，去掉/api前缀
        }
      }
    }
  }
})
