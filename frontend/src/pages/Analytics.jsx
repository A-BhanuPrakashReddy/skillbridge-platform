import { useEffect, useState } from 'react'
import { getDSAGrowth, getAptitudeGrowth, getReadinessTrend } from '../api/analytics'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts'

export default function Analytics() {
  const [dsaGrowth, setDsaGrowth] = useState(null)
  const [aptGrowth, setAptGrowth] = useState(null)
  const [readiness, setReadiness] = useState(null)

  useEffect(() => {
    getDSAGrowth().then(r => setDsaGrowth(r.data.data)).catch(console.error)
    getAptitudeGrowth().then(r => setAptGrowth(r.data.data)).catch(console.error)
    getReadinessTrend().then(r => setReadiness(r.data.data)).catch(console.error)
  }, [])

  const dsaData = dsaGrowth?.weeks?.map((w, i) => ({ week: `W${i+1}`, solved: dsaGrowth.solved[i] })) || []
  const aptData = aptGrowth?.weeks?.map((w, i) => ({ week: `W${i+1}`, score: aptGrowth.avgScores[i]?.toFixed(1) })) || []
  const readinessData = readiness?.dates?.map((d, i) => ({ date: d, score: readiness.scores[i]?.toFixed(1) })) || []

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Analytics</h1>

      {readiness && (
        <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
          <h3 className="font-semibold mb-1">Readiness Trend</h3>
          <p className="text-sm text-gray-500 mb-4">Current: {parseFloat(readiness.currentScore || 0).toFixed(1)} | Growth: +{parseFloat(readiness.improvement || 0).toFixed(1)}</p>
          <ResponsiveContainer width="100%" height={200}>
            <LineChart data={readinessData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" tick={{ fontSize: 11 }} />
              <YAxis domain={[0, 100]} tick={{ fontSize: 11 }} />
              <Tooltip />
              <Line type="monotone" dataKey="score" stroke="#6366f1" strokeWidth={2} dot={{ r: 4 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}

      <div className="grid md:grid-cols-2 gap-5">
        <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
          <h3 className="font-semibold mb-4">DSA Weekly Progress</h3>
          {dsaData.length > 0 ? (
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={dsaData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="week" tick={{ fontSize: 11 }} />
                <YAxis tick={{ fontSize: 11 }} />
                <Tooltip />
                <Bar dataKey="solved" fill="#6366f1" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : <p className="text-center text-gray-400 py-16 text-sm">No DSA data yet</p>}
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
          <h3 className="font-semibold mb-4">Aptitude Weekly Scores</h3>
          {aptData.length > 0 ? (
            <ResponsiveContainer width="100%" height={200}>
              <LineChart data={aptData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="week" tick={{ fontSize: 11 }} />
                <YAxis domain={[0, 100]} tick={{ fontSize: 11 }} />
                <Tooltip />
                <Line type="monotone" dataKey="score" stroke="#a855f7" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          ) : <p className="text-center text-gray-400 py-16 text-sm">No aptitude data yet</p>}
        </div>
      </div>
    </div>
  )
}
