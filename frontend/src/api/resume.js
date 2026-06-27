import api from './axios'
export const uploadResume = (formData) => api.post('/api/resume/upload', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})
export const getMyResumes = () => api.get('/api/resume/my')
export const getLatestResume = () => api.get('/api/resume/latest')
