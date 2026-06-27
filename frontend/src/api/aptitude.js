import api from './axios'
export const startQuiz = (data) => api.post('/api/aptitude/quiz/start', data)
export const submitQuiz = (attemptId, data) => api.post(`/api/aptitude/quiz/${attemptId}/submit`, data)
export const getMyAttempts = () => api.get('/api/aptitude/attempts')
export const getAptitudeStats = () => api.get('/api/aptitude/stats')
