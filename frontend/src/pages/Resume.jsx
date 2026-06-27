import { useEffect, useState, useRef } from 'react'
import { getMyResumes, getLatestResume, uploadResume } from '../api/resume'
import toast from 'react-hot-toast'
import { Upload, FileText, CheckCircle2, AlertTriangle } from 'lucide-react'

export default function Resume() {
  const [resumes, setResumes] = useState([])
  const [latest, setLatest] = useState(null)
  const [uploading, setUploading] = useState(false)
  const fileRef = useRef()

  const load = async () => {
    try {
      const [r, l] = await Promise.all([getMyResumes(), getLatestResume()])
      setResumes(r.data.data || [])
      setLatest(l.data.data)
    } catch (e) { console.error(e) }
  }

  useEffect(() => { load() }, [])

  const handleUpload = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    if (!file.name.endsWith('.pdf')) { toast.error('Only PDF files allowed'); return }
    setUploading(true)
    const formData = new FormData()
    formData.append('file', file)
    try {
      await uploadResume(formData)
      toast.success('Resume uploaded and analyzed!')
      load()
    } catch (er) {
      toast.error(er.response?.data?.message || 'Upload failed')
    } finally { setUploading(false) }
  }

  const score = parseFloat(latest?.atsScore || 0)
  const scoreColor = score >= 70 ? 'text-green-600' : score >= 40 ? 'text-yellow-600' : 'text-red-600'
  const feedback = latest?.atsFeedback ? (typeof latest.atsFeedback === 'string' ? JSON.parse(latest.atsFeedback) : latest.atsFeedback) : null

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Resume &amp; ATS</h1>

      <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <h3 className="font-semibold text-gray-900 mb-4">Upload Resume</h3>
        <div
          className="border-2 border-dashed border-gray-200 rounded-xl p-8 text-center cursor-pointer hover:border-indigo-400 transition-colors"
          onClick={() => fileRef.current?.click()}
        >
          <Upload size={32} className="mx-auto text-gray-400 mb-2" />
          <p className="text-gray-600 font-medium">Drop your PDF here or click to browse</p>
          <p className="text-gray-400 text-sm mt-1">Max 10MB • PDF only</p>
          <input ref={fileRef} type="file" accept=".pdf" onChange={handleUpload} className="hidden" />
        </div>
        {uploading && <p className="text-center text-indigo-600 mt-3 font-medium">Analyzing resume with ATS...</p>}
      </div>

      {latest && (
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-gray-900">Latest ATS Analysis</h3>
            <span className={`text-3xl font-bold ${scoreColor}`}>{score.toFixed(0)}%</span>
          </div>
          <div className="bg-gray-100 rounded-full h-3 mb-4">
            <div className={`h-3 rounded-full transition-all ${score >= 70 ? 'bg-green-500' : score >= 40 ? 'bg-yellow-500' : 'bg-red-500'}`}
              style={{ width: `${Math.min(score, 100)}%` }}></div>
          </div>
          {feedback && (
            <div className="grid md:grid-cols-2 gap-4">
              {feedback.strengths?.length > 0 && (
                <div>
                  <h4 className="font-medium text-green-700 mb-2 flex items-center gap-1"><CheckCircle2 size={14} /> Strengths</h4>
                  <ul className="text-sm text-gray-600 space-y-1">{feedback.strengths.map((s, i) => <li key={i}>• {s}</li>)}</ul>
                </div>
              )}
              {feedback.suggestions?.length > 0 && (
                <div>
                  <h4 className="font-medium text-orange-700 mb-2 flex items-center gap-1"><AlertTriangle size={14} /> Suggestions</h4>
                  <ul className="text-sm text-gray-600 space-y-1">{feedback.suggestions.map((s, i) => <li key={i}>• {s}</li>)}</ul>
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {resumes.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          <h3 className="font-semibold text-gray-900 p-4 border-b">Upload History</h3>
          <div className="divide-y">
            {resumes.map(r => (
              <div key={r.id} className="flex items-center justify-between p-4 hover:bg-gray-50">
                <div className="flex items-center gap-3">
                  <FileText size={18} className="text-gray-400" />
                  <div>
                    <p className="text-sm font-medium">{r.fileName || 'Resume'}</p>
                    <p className="text-xs text-gray-500">Version {r.versionNumber}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className={`text-sm font-bold ${parseFloat(r.atsScore || 0) >= 70 ? 'text-green-600' : 'text-yellow-600'}`}>
                    {parseFloat(r.atsScore || 0).toFixed(0)}%
                  </p>
                  {r.isLatest && <span className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">Latest</span>}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
