import api from './axios'
export const getDSAGrowth = () => api.get('/api/analytics/dsa-growth')
export const getAptitudeGrowth = () => api.get('/api/analytics/aptitude-growth')
export const getResumeHistory = () => api.get('/api/analytics/resume-history')
export const getReadinessTrend = () => api.get('/api/analytics/readiness-trend')
