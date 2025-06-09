// src/utils/request.js
import axios from 'axios'
import {Message, MessageBox} from 'element-ui' // 如果你使用Element UI

// 创建axios实例
const service = axios.create({
    baseURL: '/api', // 从环境变量读取API地址
    timeout: 10000 // 请求超时时间
})

// request拦截器
service.interceptors.request.use(
    config => {
        // 从sessionStorage获取token
        const token = sessionStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = token
        }
        return config
    },
    error => {
        console.log(error) // for debug
        return Promise.reject(error)
    }
)

// response拦截器
service.interceptors.response.use(
    response => {
        const res = response.data

        // 这里根据你的后端返回结构进行调整
        if (res.code !== 200 && res.success !== true) {
            Message({
                message: res.message || 'Error',
                type: 'error',
                duration: 5 * 1000
            })

            // 特定状态码处理
            if (res.code === 401 || res.code === 403) {
                // 重新登录
                MessageBox.confirm('登录状态已过期，请重新登录', '确认登出', {
                    confirmButtonText: '重新登录',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    sessionStorage.removeItem('token')
                    location.reload()
                })
            }
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res
        }
    },
    error => {
        console.log('err' + error) // for debug
        Message({
            message: error.message,
            type: 'error',
            duration: 5 * 1000
        })
        return Promise.reject(error)
    }
)

export default service