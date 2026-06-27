import { useEffect, useState } from 'react'
import { getOfficerAnalytics, getStudents } from '../api/officer'
import { Users, TrendingUp, Award, AlertCircle } from 'lucide-react'

export default function OfficerDashboard() {
  const [analytics, setAnalytics] = useState(null)
  const [students, setStudents] = useState([])

  useEffect(() => {
    getOfficerAnalytics().then(r => setAnalytics(r.data.data)).catch(console.error)
    getStudents({ page: 0, size: 5 }).then(r => setStudents(r.data.data?.content || [])).catch(console.error)
  }, [])

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Officer Dashboard</h1>
      {analytics && (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {[
            { label: 'Total Students', value: analytics.totalStudents, icon: Users, color: 'bg-blue-500' },
            { label: 'Avg Readiness', value: `${(analytics.avgReadinessScore || 0).toFixed(1)}%`, icon: TrendingUp, color: 'bg-purple-500' },
            { label: 'Above 70%', value: analytics.studentsAbove70, icon: Award, color: 'bg-green-500' },
            { label: 'Below 50%', value: analytics.studentsBelow50, icon: AlertCircle, color: 'bg-red-500' },
          ].map(s => (
            <div key={s.label} className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
              <div className="flex items-center justify-between mb-2">
                <span className="text-sm text-gray-500">{s.label}</span>
                <div className={`p-2 rounded-lg ${s.color}`}><s.icon size={14} className="text-white" /></div>
              </div>
              <p className="text-2xl font-bold">{s.value}</p>
            </div>
          ))}
        </div>
      )}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <h3 className="font-semibold p-4 border-b">Recent Students</h3>
        <table className="w-full text-sm">
          <thead className="bg-gray-50"><tr>
            <th className="px-4 py-2 text-left text-gray-600 font-medium">Name</th>
            <th className="px-4 py-2 text-left text-gray-600 font-medium">Branch</th>
            <th className="px-4 py-2 text-left text-gray-600 font-medium">CGPA</th>
            <th className="px-4 py-2 text-left text-gray-600 font-medium">Readiness</th>
          </tr></thead>
          <tbody className="divide-y divide-gray-50">
            {students.map(s => (
              <tr key={s.id} className="hover:bg-gray-50">
                <td className="px-4 py-2 font-medium">{s.name}</td>
                <td className="px-4 py-2 text-gray-500">{s.branch || '—'}</td>
                <td className="px-4 py-2">{parseFloat(s.cgpa || 0).toFixed(2)}</td>
                <td className="px-4 py-2">
                  <span className={`font-medium ${parseFloat(s.readinessScore || 0) >= 70 ? 'text-green-600' : parseFloat(s.readinessScore || 0) >= 40 ? 'text-yellow-600' : 'text-red-600'}`}>
                    {parseFloat(s.readinessScore || 0).toFixed(1)}%
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
