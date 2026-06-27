import api from './axios'
export const getStudents = (params) => api.get('/api/officer/students', { params })
export const getStudentDetail = (id) => api.get(`/api/officer/students/${id}`)
export const getOfficerAnalytics = () => api.get('/api/officer/analytics')
