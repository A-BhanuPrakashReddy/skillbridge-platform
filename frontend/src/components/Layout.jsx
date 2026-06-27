import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  LayoutDashboard, Code2, Brain, FileText, Building2,
  Calendar, BarChart3, Users, Settings, LogOut, Menu, X, ChevronRight
} from 'lucide-react'

const studentNav = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/dsa', icon: Code2, label: 'DSA Practice' },
  { to: '/aptitude', icon: Brain, label: 'Aptitude' },
  { to: '/resume', icon: FileText, label: 'Resume' },
  { to: '/companies', icon: Building2, label: 'Companies' },
  { to: '/interviews', icon: Calendar, label: 'Interviews' },
  { to: '/analytics', icon: BarChart3, label: 'Analytics' },
  { to: '/profile', icon: Settings, label: 'Profile' },
]

const officerNav = [
  { to: '/officer', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/officer/students', icon: Users, label: 'Students' },
  { to: '/officer/interviews', icon: Calendar, label: 'Interviews' },
]

export default function Layout({ children }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [sidebarOpen, setSidebarOpen] = useState(true)

  const isOfficer = user?.role === 'PLACEMENT_OFFICER' || user?.role === 'ADMIN'
  const nav = isOfficer ? officerNav : studentNav

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <aside className={`${sidebarOpen ? 'w-64' : 'w-16'} transition-all duration-300 bg-white border-r border-gray-200 flex flex-col`}>
        <div className="p-4 flex items-center justify-between border-b border-gray-100">
          {sidebarOpen && <span className="text-xl font-bold text-indigo-600">SkillBridge</span>}
          <button onClick={() => setSidebarOpen(!sidebarOpen)} className="p-1.5 rounded-lg hover:bg-gray-100">
            {sidebarOpen ? <X size={18} /> : <Menu size={18} />}
          </button>
        </div>

        <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
          {nav.map(item => {
            const active = location.pathname === item.to || location.pathname.startsWith(item.to + '/')
            return (
              <Link
                key={item.to}
                to={item.to}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  active ? 'bg-indigo-50 text-indigo-700' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                }`}
              >
                <item.icon size={18} className="shrink-0" />
                {sidebarOpen && <span>{item.label}</span>}
                {sidebarOpen && active && <ChevronRight size={14} className="ml-auto" />}
              </Link>
            )
          })}
        </nav>

        <div className="p-3 border-t border-gray-100">
          {sidebarOpen && (
            <div className="mb-2 px-3 py-2">
              <p className="text-sm font-semibold text-gray-800 truncate">{user?.name}</p>
              <p className="text-xs text-gray-500 truncate">{user?.role}</p>
            </div>
          )}
          <button
            onClick={handleLogout}
            className="flex items-center gap-3 w-full px-3 py-2 rounded-lg text-sm text-gray-600 hover:bg-red-50 hover:text-red-600 transition-colors"
          >
            <LogOut size={18} className="shrink-0" />
            {sidebarOpen && <span>Logout</span>}
          </button>
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 overflow-auto">
        <div className="p-6">
          {children}
        </div>
      </main>
    </div>
  )
}
