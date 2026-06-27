import { useEffect, useState } from 'react'
import { getOfficerInterviews, submitFeedback } from '../api/interviews'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'

export default function OfficerInterviews() {
  const [interviews, setInterviews] = useState([])
  const [selected, setSelected] = useState(null)
  const { register, handleSubmit, reset } = useForm()

  const load = () => getOfficerInterviews().then(r => setInterviews(r.data.data || [])).catch(console.error)
  useEffect(() => { load() }, [])

  const onFeedback = async (data) => {
    try {
      await submitFeedback(selected.id, { score: parseFloat(data.score), feedback: data.feedback })
      toast.success('Feedback submitted!')
      setSelected(null)
      reset()
      load()
    } catch (e) { toast.error(e.response?.data?.message || 'Failed') }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Interview Management</h1>
      <div className="space-y-3">
        {interviews.length === 0 ? (
          <div className="text-center py-12 text-gray-400 bg-white rounded-xl border">No interviews yet</div>
        ) : interviews.map(i => (
          <div key={i.id} className="bg-white rounded-xl p-4 border border-gray-100 flex items-center justify-between shadow-sm">
            <div>
              <p className="font-medium">{i.studentName}</p>
              <p className="text-sm text-gray-500">{new Date(i.scheduledAt).toLocaleString()} • {i.interviewType}</p>
              {i.score != null && <p className="text-xs text-green-600">Score: {i.score}/100</p>}
            </div>
            <div className="flex items-center gap-3">
              <span className={`text-xs px-2 py-1 rounded-full ${i.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' : i.status === 'COMPLETED' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'}`}>{i.status}</span>
              {i.status !== 'COMPLETED' && (
                <button onClick={() => setSelected(i)} className="text-sm bg-indigo-600 text-white px-3 py-1 rounded-lg hover:bg-indigo-700 transition-colors">
                  Give Feedback
                </button>
              )}
            </div>
          </div>
        ))}
      </div>

      {selected && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-md p-6 shadow-2xl">
            <h3 className="font-semibold mb-4">Feedback for {selected.studentName}</h3>
            <form onSubmit={handleSubmit(onFeedback)} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Score (0-100)</label>
                <input type="number" {...register('score', { required: true, min: 0, max: 100 })} min="0" max="100"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 outline-none" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Feedback</label>
                <textarea {...register('feedback')} rows={4}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 outline-none resize-none" />
              </div>
              <div className="flex gap-3">
                <button type="submit" className="flex-1 bg-indigo-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">Submit</button>
                <button type="button" onClick={() => setSelected(null)} className="px-4 border border-gray-300 rounded-lg text-sm hover:bg-gray-50 transition-colors">Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
