import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Layout from './components/Layout'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import DSA from './pages/DSA'
import Aptitude from './pages/Aptitude'
import Resume from './pages/Resume'
import Companies from './pages/Companies'
import Interviews from './pages/Interviews'
import Analytics from './pages/Analytics'
import Profile from './pages/Profile'
import OfficerDashboard from './pages/OfficerDashboard'
import OfficerStudents from './pages/OfficerStudents'
import OfficerInterviews from './pages/OfficerInterviews'

function StudentRoutes() {
  return (
    <ProtectedRoute roles={['STUDENT']}>
      <Layout>
        <Routes>
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="dsa" element={<DSA />} />
          <Route path="aptitude" element={<Aptitude />} />
          <Route path="resume" element={<Resume />} />
          <Route path="companies" element={<Companies />} />
          <Route path="interviews" element={<Interviews />} />
          <Route path="analytics" element={<Analytics />} />
          <Route path="profile" element={<Profile />} />
          <Route path="*" element={<Navigate to="dashboard" replace />} />
        </Routes>
      </Layout>
    </ProtectedRoute>
  )
}

function OfficerRoutes() {
  return (
    <ProtectedRoute roles={['PLACEMENT_OFFICER', 'ADMIN']}>
      <Layout>
        <Routes>
          <Route path="officer" element={<OfficerDashboard />} />
          <Route path="officer/students" element={<OfficerStudents />} />
          <Route path="officer/interviews" element={<OfficerInterviews />} />
          <Route path="*" element={<Navigate to="officer" replace />} />
        </Routes>
      </Layout>
    </ProtectedRoute>
  )
}

function RouterSwitch() {
  const { user, loading } = useAuth()
  if (loading) return <div className="flex items-center justify-center h-screen"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div></div>
  if (!user) return <Navigate to="/login" replace />
  if (user.role === 'STUDENT') return <StudentRoutes />
  return <OfficerRoutes />
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/*" element={<RouterSwitch />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
