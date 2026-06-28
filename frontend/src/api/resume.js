import api from './axios'

export const uploadResume = (data) =>
  api.post('/api/resume/upload', data)

export const getMyResumes = () =>
  api.get('/api/resume/my')

export const getLatestResume = () =>
  api.get('/api/resume/my/latest')