import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('trademart_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('trademart_token')
      localStorage.removeItem('trademart_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api