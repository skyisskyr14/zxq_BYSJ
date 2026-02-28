// src/utils/request.js
import axios from 'axios'
import { Message } from 'element-ui' // 可选：未用可移除

const service = axios.create({
  baseURL: 'http://localhost:10438/api', // 如果用 devServer 代理，改成 '/api'
  timeout: 10000,
  withCredentials: true
})

// 请求拦截：按 data 类型设置 Content-Type
service.interceptors.request.use(
  (config) => {
    const isFormData =
      typeof FormData !== 'undefined' && config.data instanceof FormData

    if (isFormData) {
      // 关键点1：不要自己写 Content-Type，交给浏览器自动带 boundary
      if (config.headers && config.headers['Content-Type']) {
        delete config.headers['Content-Type']
      }
      // 关键点2：不要对 FormData 做任何 transform（保持原样）
      config.transformRequest = [(d) => d]
    } else {
      // 非 FormData（普通 JSON）时再加 JSON 头
      config.headers['Content-Type'] = 'application/json'
      // axios 默认会做 JSON 序列化，这里无需手工 stringify
    }

    // 如果需要加 token：
    // const token = localStorage.getItem('userToken')
    // if (token) config.headers['Authorization'] = `Bearer ${token}`

    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截：保持 Promise 语义清晰（失败用 reject）
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 你们的后端是 { code, data, message } 这种
    if (res && typeof res.code !== 'undefined' && res.code !== 200) {
      // 这里不弹也行，让调用处统一处理
      // Message.error(res.message || '请求失败')
      return Promise.resolve(res)
    }
    return Promise.resolve(res || response)
  },
  (error) => {
    // Message.error(error?.message || '网络异常')
    return Promise.reject(error)
  }
)

export default service
