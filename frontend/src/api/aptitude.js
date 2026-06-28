import api from './axios'

export const startQuiz = (data) =>
  api.post('/api/aptitude/quiz/start', data)

export const submitQuiz = (data) =>
  api.post('/api/aptitude/quiz/submit', data)

export const getMyAttempts = () =>
  api.get('/api/aptitude/attempts/my')

export const getAptitudeStats = () =>
  api.get('/api/aptitude/stats')