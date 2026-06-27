import { useEffect, useState } from 'react'
import { getDashboard } from '../api/student'
import { Code2, Brain, FileText, Calendar, Target, Award } from 'lucide-react'

function StatCard({ icon: Icon, label, value, sub, color }) {
  return (
    <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
      <div className="flex items-center justify-between mb-3">
        <span className="text-sm text-gray-500 font-medium">{label}</span>
        <div className={`p-2 rounded-lg ${color}`}>
          <Icon size={16} className="text-white" />
        </div>
      </div>
      <p className="text-2xl font-bold text-gray-900">{value ?? '—'}</p>
      {sub && <p className="text-xs text-gray-400 mt-1">{sub}</p>}
    </div>
  )
}

export default function Dashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getDashboard()
      .then(r => setData(r.data.data))
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div></div>
  if (!data) return <div className="text-center text-gray-500 py-20">Failed to load dashboard</div>

  const readiness = parseFloat(data.readinessScore || 0)

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Welcome back, {data.name?.split(' ')[0]}!</h1>
        <p className="text-gray-500 text-sm mt-1">{data.branch} • CGPA: {parseFloat(data.cgpa || 0).toFixed(2)}</p>
      </div>

      {/* Readiness score hero */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 rounded-2xl p-6 text-white">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-indigo-200 text-sm font-medium">Placement Readiness Score</p>
            <p className="text-5xl font-bold mt-1">{readiness.toFixed(1)}<span className="text-2xl text-indigo-200">/100</span></p>
            <p className="text-indigo-200 text-sm mt-2">
              {readiness >= 70 ? 'Strong — You are well prepared!' : readiness >= 40 ? 'Average — Keep improving!' : 'Weak — Need more practice'}
            </p>
          </div>
          <div className="text-right">
            <div className="bg-white/20 rounded-xl p-4">
              <Target size={48} className="text-white" />
            </div>
          </div>
        </div>
        <div className="mt-4 bg-white/20 rounded-full h-2">
          <div className="bg-white rounded-full h-2 transition-all" style={{ width: `${Math.min(readiness, 100)}%` }}></div>
        </div>
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <StatCard icon={Code2} label="DSA Solved" value={`${data.totalSolved}/${data.totalProblems}`} sub={`${data.currentStreak} day streak`} color="bg-blue-500" />
        <StatCard icon={Brain} label="Avg Aptitude" value={`${(data.avgAptitudeScore || 0).toFixed(1)}%`} sub={`${data.totalQuizAttempts} quizzes`} color="bg-purple-500" />
        <StatCard icon={FileText} label="ATS Score" value={data.latestAtsScore ? `${parseFloat(data.latestAtsScore).toFixed(0)}%` : 'No resume'} sub={data.hasResume ? 'Resume uploaded' : 'Upload to score'} color="bg-green-500" />
        <StatCard icon={Calendar} label="Interviews" value={data.pendingInterviews} sub={`${data.completedInterviews} completed`} color="bg-orange-500" />
      </div>

      {/* Companies */}
      <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="font-semibold text-gray-900">Available Companies</h3>
            <p className="text-3xl font-bold text-indigo-600 mt-1">{data.totalCompaniesCount}</p>
            <p className="text-sm text-gray-500">companies recruiting</p>
          </div>
          <Award size={48} className="text-indigo-200" />
        </div>
      </div>
    </div>
  )
}
