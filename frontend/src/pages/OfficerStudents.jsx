import { useEffect, useState } from 'react'
import { getStudents, getStudentDetail } from '../api/officer'
import { X } from 'lucide-react'

export default function OfficerStudents() {
  const [students, setStudents] = useState([])
  const [filter, setFilter] = useState({ page: 0, size: 20 })
  const [selected, setSelected] = useState(null)
  const [detail, setDetail] = useState(null)
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    getStudents(filter)
      .then(r => setStudents(r.data.data?.content || r.data.data || []))
      .catch(console.error)
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [filter])

  const handleSelect = async (s) => {
    setSelected(s)
    try {
      const res = await getStudentDetail(s.id)
      setDetail(res.data.data)
    } catch (e) { console.error(e) }
  }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Students</h1>
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        {loading ? (
          <div className="flex items-center justify-center h-40"><div className="animate-spin rounded-full h-6 w-6 border-b-2 border-indigo-600"></div></div>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b"><tr>
              <th className="px-4 py-3 text-left text-gray-600 font-medium">Name</th>
              <th className="px-4 py-3 text-left text-gray-600 font-medium">Email</th>
              <th className="px-4 py-3 text-left text-gray-600 font-medium">Branch</th>
              <th className="px-4 py-3 text-left text-gray-600 font-medium">CGPA</th>
              <th className="px-4 py-3 text-left text-gray-600 font-medium">Readiness</th>
              <th className="px-4 py-3"></th>
            </tr></thead>
            <tbody className="divide-y divide-gray-50">
              {students.map(s => (
                <tr key={s.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium">{s.name}</td>
                  <td className="px-4 py-3 text-gray-500">{s.email}</td>
                  <td className="px-4 py-3 text-gray-500">{s.branch || '—'}</td>
                  <td className="px-4 py-3">{parseFloat(s.cgpa || 0).toFixed(2)}</td>
                  <td className="px-4 py-3">
                    <span className={`font-medium ${parseFloat(s.readinessScore || 0) >= 70 ? 'text-green-600' : parseFloat(s.readinessScore || 0) >= 40 ? 'text-yellow-600' : 'text-red-600'}`}>
                      {parseFloat(s.readinessScore || 0).toFixed(1)}%
                    </span>
                  </td>
                  <td className="px-4 py-3"><button onClick={() => handleSelect(s)} className="text-indigo-600 text-xs hover:underline">View</button></td>
                </tr>
              ))}
              {students.length === 0 && <tr><td colSpan={6} className="text-center py-10 text-gray-400">No students found</td></tr>}
            </tbody>
          </table>
        )}
      </div>

      {selected && detail && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-lg p-6 shadow-2xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-semibold">{detail.name}</h3>
              <button onClick={() => { setSelected(null); setDetail(null) }}><X size={18} className="text-gray-400" /></button>
            </div>
            <div className="grid grid-cols-2 gap-3 text-sm">
              {[['Branch', detail.branch], ['CGPA', parseFloat(detail.cgpa || 0).toFixed(2)], ['Backlogs', detail.activeBacklogs], ['Readiness', `${parseFloat(detail.readinessScore || 0).toFixed(1)}%`],
                ['DSA Solved', detail.dsaSolved], ['Quizzes', detail.quizzesTaken], ['Interviews', detail.interviewsCompleted], ['Resumes', detail.resumesUploaded],
                ['Avg Aptitude', `${(detail.avgAptitudeScore || 0).toFixed(1)}%`], ['Avg Interview', `${(detail.avgInterviewScore || 0).toFixed(1)}/100`]
              ].map(([k, v]) => (
                <div key={k} className="bg-gray-50 rounded-lg p-3">
                  <p className="text-xs text-gray-500">{k}</p>
                  <p className="font-semibold text-gray-900">{v ?? '—'}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
