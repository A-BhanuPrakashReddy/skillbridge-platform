import { useEffect, useState } from 'react'
import { getProfile, updateProfile, uploadPhoto } from '../api/student'
import { useForm } from 'react-hook-form'
import { useAuth } from '../context/AuthContext'
import toast from 'react-hot-toast'
import { Camera, Save } from 'lucide-react'

export default function Profile() {
  const { user } = useAuth()
  const { register, handleSubmit, setValue } = useForm()
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    getProfile().then(r => {
      const p = r.data.data
      setProfile(p)
      setValue('college', p.college)
      setValue('branch', p.branch)
      setValue('graduationYear', p.graduationYear)
      setValue('cgpa', p.cgpa)
      setValue('activeBacklogs', p.activeBacklogs)
      setValue('phone', p.phone)
      setValue('linkedinUrl', p.linkedinUrl)
      setValue('githubUrl', p.githubUrl)
      setValue('skills', p.skills?.join(','))
    }).catch(console.error).finally(() => setLoading(false))
  }, [])

  const onSubmit = async (data) => {
    setSaving(true)
    try {
      const skills = data.skills ? data.skills.split(',').map(s => s.trim()).filter(Boolean) : []
      await updateProfile({ ...data, skills })
      toast.success('Profile updated!')
    } catch (e) { toast.error(e.response?.data?.message || 'Update failed') }
    finally { setSaving(false) }
  }

  const handlePhotoUpload = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    const fd = new FormData()
    fd.append('file', file)
    try {
      await uploadPhoto(fd)
      toast.success('Photo updated!')
    } catch (e) { toast.error('Photo upload failed') }
  }

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div></div>

  return (
    <div className="space-y-6 max-w-2xl">
      <h1 className="text-2xl font-bold text-gray-900">My Profile</h1>

      <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <div className="flex items-center gap-4 mb-6">
          <div className="relative">
            <div className="w-16 h-16 rounded-full bg-indigo-100 flex items-center justify-center overflow-hidden">
              {profile?.photoUrl ? <img src={profile.photoUrl} alt="" className="w-full h-full object-cover" />
                : <span className="text-2xl font-bold text-indigo-600">{user?.name?.[0]?.toUpperCase()}</span>}
            </div>
            <label className="absolute -bottom-1 -right-1 bg-white border border-gray-200 rounded-full p-1 cursor-pointer hover:bg-gray-50">
              <Camera size={12} />
              <input type="file" accept="image/*" onChange={handlePhotoUpload} className="hidden" />
            </label>
          </div>
          <div>
            <p className="font-semibold text-gray-900">{user?.name}</p>
            <p className="text-sm text-gray-500">{user?.email}</p>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="grid grid-cols-2 gap-4">
          {[
            { name: 'college', label: 'College', full: true },
            { name: 'branch', label: 'Branch/Department' },
            { name: 'graduationYear', label: 'Graduation Year', type: 'number' },
            { name: 'cgpa', label: 'CGPA', type: 'number', step: '0.01' },
            { name: 'activeBacklogs', label: 'Active Backlogs', type: 'number' },
            { name: 'phone', label: 'Phone' },
            { name: 'linkedinUrl', label: 'LinkedIn URL', full: true },
            { name: 'githubUrl', label: 'GitHub URL', full: true },
            { name: 'skills', label: 'Skills (comma separated)', full: true },
          ].map(f => (
            <div key={f.name} className={f.full ? 'col-span-2' : ''}>
              <label className="block text-sm font-medium text-gray-700 mb-1">{f.label}</label>
              <input {...register(f.name)} type={f.type || 'text'} step={f.step}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 outline-none" />
            </div>
          ))}
          <div className="col-span-2">
            <button type="submit" disabled={saving}
              className="flex items-center gap-2 bg-indigo-600 text-white px-6 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 disabled:opacity-50 transition-colors">
              <Save size={14} /> {saving ? 'Saving...' : 'Save Profile'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
