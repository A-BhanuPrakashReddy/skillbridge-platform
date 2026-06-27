import api from './axios'
export const login = (data) => api.post('/api/auth/login', data)
export const register = (data) => api.post('/api/auth/register', data)
export const getMe = () => api.get('/api/auth/me')
export const changePassword = (data) => api.post('/api/auth/change-password', data)
