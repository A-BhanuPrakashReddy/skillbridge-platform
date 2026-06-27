import { useEffect, useState } from 'react'
import { getDSAProblems, getDSATopics, getDSAStats, getDSAStreak, markProgress } from '../api/dsa'
import toast from 'react-hot-toast'
import { CheckCircle2, Circle, RefreshCw } from 'lucide-react'

const DIFFICULTY_COLORS = {
  EASY: 'text-green-600 bg-green-50',
  MEDIUM: 'text-yellow-600 bg-yellow-50',
  HARD: 'text-red-600 bg-red-50'
}

export default function DSA() {
  const [problems, setProblems] = useState([])
  const [topics, setTopics] = useState([])
  const [stats, setStats] = useState(null)
  const [streak, setStreak] = useState(null)

  const [filter, setFilter] = useState({
    topic: '',
    difficulty: '',
    page: 0,
    size: 50   // Changed from 20 to 50
  })

  const [loading, setLoading] = useState(true)

  const load = async () => {
    setLoading(true)
    try {
      const [p, t, s, st] = await Promise.all([
        getDSAProblems(filter),
        getDSATopics(),
        getDSAStats(),
        getDSAStreak()
      ])

      console.log(p.data.data)

      setProblems(p.data.data?.content || p.data.data || [])
      setTopics(t.data.data || [])
      setStats(s.data.data)
      setStreak(st.data.data)
    } catch (e) {
      console.error(e)
    }
    setLoading(false)
  }

  useEffect(() => {
    load()
  }, [filter])

  const handleMark = async (id, status) => {
    try {
      await markProgress(id, { status })
      toast.success(`Marked as ${status.toLowerCase()}`)
      load()
    } catch (e) {
      toast.error('Failed to update')
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">DSA Practice</h1>

        <div className="flex gap-3 text-sm">
          <span className="bg-green-100 text-green-700 px-3 py-1 rounded-full font-medium">
            {streak?.currentStreak || 0} day streak
          </span>

          <span className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full font-medium">
            {stats?.solved || 0} solved
          </span>
        </div>
      </div>

      {/* Filters */}
      <div className="flex gap-3 flex-wrap">

        <select
          value={filter.topic}
          onChange={(e) =>
            setFilter(f => ({
              ...f,
              topic: e.target.value,
              page: 0
            }))
          }
          className="px-3 py-2 border border-gray-300 rounded-lg text-sm"
        >
          <option value="">All Topics</option>

          {topics.map(t => (
            <option key={t} value={t}>
              {t}
            </option>
          ))}
        </select>

        <select
          value={filter.difficulty}
          onChange={(e) =>
            setFilter(f => ({
              ...f,
              difficulty: e.target.value,
              page: 0
            }))
          }
          className="px-3 py-2 border border-gray-300 rounded-lg text-sm"
        >
          <option value="">All</option>
          <option value="EASY">Easy</option>
          <option value="MEDIUM">Medium</option>
          <option value="HARD">Hard</option>
        </select>

      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">

        {loading ? (
          <div className="flex items-center justify-center h-40">
            <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-indigo-600"></div>
          </div>
        ) : (
          <table className="w-full text-sm">

            <thead className="bg-gray-50 border-b border-gray-100">
              <tr>
                <th className="px-4 py-3 text-left">Status</th>
                <th className="px-4 py-3 text-left">Problem</th>
                <th className="px-4 py-3 text-left">Topic</th>
                <th className="px-4 py-3 text-left">Difficulty</th>
                <th className="px-4 py-3 text-left">Actions</th>
              </tr>
            </thead>

            <tbody className="divide-y divide-gray-50">

              {problems.map(p => (
                <tr key={p.id}>

                  <td className="px-4 py-3">
                    {p.userStatus === 'SOLVED'
                      ? <CheckCircle2 size={18} className="text-green-500" />
                      : p.userStatus === 'REVISIT'
                        ? <RefreshCw size={18} className="text-orange-500" />
                        : <Circle size={18} className="text-gray-300" />}
                  </td>

                  <td className="px-4 py-3">
                    <a
                      href={p.problemUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="font-medium text-indigo-600"
                    >
                      {p.title}
                    </a>
                  </td>

                  <td className="px-4 py-3">{p.topic}</td>

                  <td className="px-4 py-3">
                    <span className={`px-2 py-1 rounded-full text-xs ${DIFFICULTY_COLORS[p.difficulty]}`}>
                      {p.difficulty}
                    </span>
                  </td>

                  <td className="px-4 py-3">
                    <div className="flex gap-2">

                      <button
                        onClick={() => handleMark(p.id, 'SOLVED')}
                        className="bg-green-100 text-green-700 px-2 py-1 rounded"
                      >
                        Solved
                      </button>

                      <button
                        onClick={() => handleMark(p.id, 'ATTEMPTED')}
                        className="bg-yellow-100 text-yellow-700 px-2 py-1 rounded"
                      >
                        Attempted
                      </button>

                      <button
                        onClick={() => handleMark(p.id, 'REVISIT')}
                        className="bg-orange-100 text-orange-700 px-2 py-1 rounded"
                      >
                        Revisit
                      </button>

                    </div>
                  </td>

                </tr>
              ))}

              {problems.length === 0 && (
                <tr>
                  <td colSpan={5} className="text-center py-10">
                    No problems found.
                  </td>
                </tr>
              )}

            </tbody>

          </table>
        )}

      </div>
    </div>
  )
}