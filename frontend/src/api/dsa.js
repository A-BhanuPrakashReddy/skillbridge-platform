import api from './axios'

export const getDSAProblems = (filter) => {
    const params = {
        page: filter.page,
        size: filter.size,
    }

    // Only send topic if a specific topic is selected
    if (filter.topic && filter.topic.trim() !== '') {
        params.topic = filter.topic
    }

    // Only send difficulty if a specific difficulty is selected
    if (filter.difficulty) {
        params.difficulty = filter.difficulty
    }

    return api.get('/api/dsa/problems', { params })
}

export const getDSATopics = () =>
    api.get('/api/dsa/topics')

export const getDSAStats = () =>
    api.get('/api/dsa/stats')

export const getDSAStreak = () =>
    api.get('/api/dsa/streak')

export const markProgress = (problemId, data) =>
    api.post('/api/dsa/progress', {
        problemId,
        ...data
    })

export const getWeakTopics = () =>
    api.get('/api/dsa/weak-topics')