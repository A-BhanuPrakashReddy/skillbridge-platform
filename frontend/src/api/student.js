import api from './axios'
export const getProfile = () => api.get('/api/students/profile')
export const updateProfile = (data) => api.put('/api/students/profile', data)
export const getDashboard = () => api.get('/api/students/dashboard')
export const getReadinessScore = () => api.get('/api/students/readiness-score')
export const uploadPhoto = (formData) => api.post('/api/students/profile/photo', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})
