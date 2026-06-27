import api from './axios'
export const bookSlot = (data) => api.post('/api/interviews/book', data)
export const getMyInterviews = () => api.get('/api/interviews/my')
export const getOfficerInterviews = () => api.get('/api/interviews/officer')
export const submitFeedback = (id, data) => api.put(`/api/interviews/${id}/feedback`, data)
