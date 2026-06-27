import { useEffect, useState } from 'react'
import { getMyInterviews, bookSlot } from '../api/interviews'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { Calendar, Clock, Plus } from 'lucide-react'

const STATUS_COLORS = { PENDING: 'bg-yellow-100 text-yellow-700', APPROVED: 'bg-blue-100 text-blue-700', COMPLETED: 'bg-green-100 text-green-700', CANCELLED: 'bg-red-100 text-red-700' }

export default function Interviews() {
  const [interviews, setInterviews] = useState([])
  const [showForm, setShowForm] = useState(false)
  const { register, handleSubmit, reset, formState: { errors } } = useForm()

  const load = () => getMyInterviews().then(r => setInterviews(r.data.data || [])).catch(console.error)
  useEffect(() => { load() }, [])

  const onSubmit = async (data) => {
    try {
      await bookSlot({ scheduledAt: new Date(data.scheduledAt).toISOString(), interviewType: data.interviewType })
      toast.success('Interview slot booked!')
      reset()
      setShowForm(false)
      load()
    } catch (e) { toast.error(e.response?.data?.message || 'Booking failed') }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Mock Interviews</h1>
        <button onClick={() => setShowForm(!showForm)}
          className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">
          <Plus size={16} /> Book Slot
        </button>
      </div>

      {showForm && (
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <h3 className="font-semibold mb-4">Book Interview Slot</h3>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date &amp; Time</label>
              <input type="datetime-local" {...register('scheduledAt', { required: 'Required' })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 outline-none" />
              {errors.scheduledAt && <p className="text-red-500 text-xs mt-1">{errors.scheduledAt.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Interview Type</label>
              <select {...register('interviewType')} className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 outline-none">
                <option value="TECHNICAL">Technical</option>
                <option value="HR">HR</option>
                <option value="APTITUDE">Aptitude</option>
              </select>
            </div>
            <div className="flex gap-3">
              <button type="submit" className="flex-1 bg-indigo-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">Book</button>
              <button type="button" onClick={() => setShowForm(false)} className="px-4 border border-gray-300 rounded-lg text-sm hover:bg-gray-50 transition-colors">Cancel</button>
            </div>
          </form>
        </div>
      )}

      <div className="space-y-3">
        {interviews.length === 0 ? (
          <div className="text-center py-12 text-gray-400 bg-white rounded-xl border border-gray-100">No interviews scheduled yet</div>
        ) : interviews.map(i => (
          <div key={i.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="p-2 bg-indigo-50 rounded-lg"><Calendar size={18} className="text-indigo-600" /></div>
              <div>
                <p className="font-medium text-sm">{i.interviewType} Interview</p>
                <p className="text-xs text-gray-500 flex items-center gap-1"><Clock size={11} /> {new Date(i.scheduledAt).toLocaleString()}</p>
                {i.score != null && <p className="text-xs text-green-600 font-medium">Score: {i.score}/100</p>}
              </div>
            </div>
            <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${STATUS_COLORS[i.status] || 'bg-gray-100 text-gray-600'}`}>{i.status}</span>
          </div>
        ))}
      </div>
    </div>
  )
}
