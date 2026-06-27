import api from './axios'
export const getCompanies = (params) => api.get('/api/companies', { params })
export const getCompany = (id) => api.get(`/api/companies/${id}`)
export const checkEligibility = (companyId) => api.get(`/api/eligibility/company/${companyId}`)
export const getEligibleCompanies = () => api.get('/api/eligibility/eligible')
