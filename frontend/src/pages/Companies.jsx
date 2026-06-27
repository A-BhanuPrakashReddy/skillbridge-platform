import { useEffect, useState } from 'react'
import { getCompanies, checkEligibility } from '../api/companies'
import { MapPin, DollarSign, CheckCircle2, XCircle } from 'lucide-react'
import toast from 'react-hot-toast'

export default function Companies() {
  const [companies, setCompanies] = useState([])
  const [loading, setLoading] = useState(true)
  const [selected, setSelected] = useState(null)
  const [eligibility, setEligibility] = useState(null)
  const [checking, setChecking] = useState(false)

  useEffect(() => {
    getCompanies({ page: 0, size: 20 })
      .then(r => setCompanies(r.data.data?.content || r.data.data || []))
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  const handleCheck = async (company) => {
    setSelected(company)
    setChecking(true)
    try {
      const res = await checkEligibility(company.id)
      setEligibility(res.data.data)
    } catch (e) {
      toast.error('Could not check eligibility')
    } finally { setChecking(false) }
  }

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div></div>

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Companies</h1>
      <div className="grid md:grid-cols-2 gap-5">
        {companies.map(c => (
          <div key={c.id} className="bg-white rounded-xl p-5 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div className="flex items-start justify-between mb-3">
              <div>
                <h3 className="font-semibold text-gray-900 text-lg">{c.name}</h3>
                <p className="text-sm text-gray-500">{c.industry}</p>
              </div>
              <span className="bg-green-100 text-green-700 text-xs font-medium px-2 py-1 rounded-full">Active</span>
            </div>
            <div className="flex flex-wrap gap-3 text-sm text-gray-500 mb-4">
              {c.location && <span className="flex items-center gap-1"><MapPin size={13} /> {c.location}</span>}
              {c.packageLpa && <span className="flex items-center gap-1"><DollarSign size={13} /> {c.packageLpa} LPA</span>}
              {c.minCgpa && <span>Min CGPA: {c.minCgpa}</span>}
            </div>
            {c.requiredSkills && c.requiredSkills.length > 0 && (
              <div className="flex flex-wrap gap-1 mb-4">
                {(Array.isArray(c.requiredSkills) ? c.requiredSkills : c.requiredSkills.split(',')).slice(0, 5).map(s => (
                  <span key={s} className="bg-gray-100 text-gray-600 text-xs px-2 py-0.5 rounded">{s.trim()}</span>
                ))}
              </div>
            )}
            <button onClick={() => handleCheck(c)}
              className="w-full bg-indigo-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">
              Check Eligibility
            </button>
          </div>
        ))}
        {companies.length === 0 && (
          <div className="col-span-2 text-center py-12 text-gray-400">No companies found. Seed data may not be loaded yet.</div>
        )}
      </div>

      {/* Eligibility modal */}
      {selected && eligibility && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-lg p-6 shadow-2xl">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-semibold text-lg">{selected.name} — Eligibility</h3>
              <button onClick={() => { setSelected(null); setEligibility(null) }} className="text-gray-400 hover:text-gray-600">&#10005;</button>
            </div>
            <div className={`p-3 rounded-lg mb-4 ${eligibility.isEligible ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'}`}>
              <div className="flex items-center gap-2">
                {eligibility.isEligible ? <CheckCircle2 className="text-green-600" size={20} /> : <XCircle className="text-red-600" size={20} />}
                <span className={`font-semibold ${eligibility.isEligible ? 'text-green-800' : 'text-red-800'}`}>
                  {eligibility.isEligible ? 'You are ELIGIBLE!' : 'Not eligible yet'} — Match: {eligibility.matchScore}%
                </span>
              </div>
            </div>
            <div className="space-y-3">
              {eligibility.criteria?.map(c => (
                <div key={c.criterionName} className="flex items-start gap-3 p-3 bg-gray-50 rounded-lg">
                  {c.isPassed ? <CheckCircle2 size={16} className="text-green-500 mt-0.5 shrink-0" /> : <XCircle size={16} className="text-red-500 mt-0.5 shrink-0" />}
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium">{c.criterionName}</p>
                    <p className="text-xs text-gray-500">{c.message}</p>
                    <p className="text-xs text-gray-400">Required: {c.required} | Yours: {c.actual}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
