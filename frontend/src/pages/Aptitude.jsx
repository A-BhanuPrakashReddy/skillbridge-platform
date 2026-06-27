import { useEffect, useState } from 'react'
import { getAptitudeStats, getMyAttempts, startQuiz, submitQuiz } from '../api/aptitude'
import toast from 'react-hot-toast'
import { CheckCircle2 } from 'lucide-react'

export default function Aptitude() {
  const [stats, setStats] = useState(null)
  const [attempts, setAttempts] = useState([])
  const [quiz, setQuiz] = useState(null)
  const [answers, setAnswers] = useState({})
  const [loading, setLoading] = useState(true)
  const [quizLoading, setQuizLoading] = useState(false)
  const [submitted, setSubmitted] = useState(null)

  useEffect(() => {
    Promise.all([getAptitudeStats(), getMyAttempts()])
      .then(([s, a]) => {
        setStats(s.data.data)
        setAttempts(a.data.data || [])
      })
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  const handleStart = async (category) => {
    setQuizLoading(true)
    try {
      const res = await startQuiz({ category, questionCount: 10 })
      setQuiz(res.data.data)
      setAnswers({})
      setSubmitted(null)
    } catch (e) {
      toast.error(e.response?.data?.message || 'Failed to start quiz')
    } finally { setQuizLoading(false) }
  }

  const handleSubmit = async () => {
    if (!quiz) return
    const unanswered = quiz.questions.filter(q => !answers[q.id])
    if (unanswered.length > 0) {
      toast.error(`Answer all ${unanswered.length} remaining questions`)
      return
    }
    try {
      const ansArray = Object.entries(answers).map(([qId, selectedOption]) => ({ questionId: parseInt(qId), selectedOption }))
      const res = await submitQuiz(quiz.attemptId, { answers: ansArray })
      setSubmitted(res.data.data)
      setQuiz(null)
      toast.success(`Quiz completed! Score: ${res.data.data?.scorePercentage?.toFixed(1)}%`)
    } catch (e) {
      toast.error(e.response?.data?.message || 'Submit failed')
    }
  }

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div></div>

  if (quiz) {
    return (
      <div className="space-y-6 max-w-3xl mx-auto">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-bold">{quiz.category} Quiz</h2>
          <span className="text-sm text-gray-500">{Object.keys(answers).length}/{quiz.questions.length} answered</span>
        </div>
        {quiz.questions.map((q, i) => (
          <div key={q.id} className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
            <p className="font-medium text-gray-900 mb-4">Q{i+1}. {q.questionText}</p>
            <div className="space-y-2">
              {['A', 'B', 'C', 'D'].map(opt => (
                <label key={opt} className={`flex items-center gap-3 p-3 rounded-lg border cursor-pointer transition-colors ${answers[q.id] === opt ? 'border-indigo-500 bg-indigo-50' : 'border-gray-200 hover:border-gray-300'}`}>
                  <input type="radio" name={`q-${q.id}`} value={opt} checked={answers[q.id] === opt}
                    onChange={() => setAnswers(a => ({ ...a, [q.id]: opt }))} className="hidden" />
                  <span className="font-medium text-gray-600 w-5">{opt}.</span>
                  <span className="text-sm text-gray-800">{q['option' + opt]}</span>
                </label>
              ))}
            </div>
          </div>
        ))}
        <div className="flex gap-3">
          <button onClick={handleSubmit} className="flex-1 bg-indigo-600 text-white py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors">
            Submit Quiz
          </button>
          <button onClick={() => setQuiz(null)} className="px-6 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
            Cancel
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Aptitude Practice</h1>

      {submitted && (
        <div className="bg-green-50 border border-green-200 rounded-xl p-5">
          <div className="flex items-center gap-3">
            <CheckCircle2 className="text-green-600" size={24} />
            <div>
              <p className="font-semibold text-green-900">Quiz Completed!</p>
              <p className="text-green-700">Score: {parseFloat(submitted.scorePercentage || 0).toFixed(1)}% | {submitted.correctAnswers}/{submitted.totalQuestions} correct</p>
            </div>
          </div>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {['QUANTITATIVE', 'LOGICAL', 'VERBAL'].map(cat => (
          <div key={cat} className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
            <h3 className="font-semibold text-gray-900 mb-1">{cat}</h3>
            <p className="text-sm text-gray-500 mb-4">10 random questions</p>
            <button onClick={() => handleStart(cat)} disabled={quizLoading}
              className="w-full bg-indigo-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 disabled:opacity-50 transition-colors">
              {quizLoading ? 'Loading...' : 'Start Quiz'}
            </button>
          </div>
        ))}
      </div>

      {stats && (
        <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
          <h3 className="font-semibold text-gray-900 mb-3">Your Stats</h3>
          <div className="grid grid-cols-3 gap-4 text-center">
            <div><p className="text-2xl font-bold text-indigo-600">{stats.totalAttempts || 0}</p><p className="text-xs text-gray-500">Total Attempts</p></div>
            <div><p className="text-2xl font-bold text-green-600">{parseFloat(stats.overallAvgScore || 0).toFixed(1)}%</p><p className="text-xs text-gray-500">Overall Avg</p></div>
            <div><p className="text-2xl font-bold text-purple-600">{stats.categoryScores ? Object.keys(stats.categoryScores).length : 0}</p><p className="text-xs text-gray-500">Categories Tried</p></div>
          </div>
        </div>
      )}

      {attempts.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          <h3 className="font-semibold text-gray-900 p-4 border-b">Recent Attempts</h3>
          <table className="w-full text-sm">
            <thead className="bg-gray-50"><tr>
              <th className="px-4 py-2 text-left text-gray-600 font-medium">Category</th>
              <th className="px-4 py-2 text-left text-gray-600 font-medium">Score</th>
              <th className="px-4 py-2 text-left text-gray-600 font-medium">Correct</th>
            </tr></thead>
            <tbody className="divide-y divide-gray-50">
              {attempts.slice(0, 10).map(a => (
                <tr key={a.id} className="hover:bg-gray-50">
                  <td className="px-4 py-2">{a.category}</td>
                  <td className="px-4 py-2 font-medium">{parseFloat(a.scorePercentage || 0).toFixed(1)}%</td>
                  <td className="px-4 py-2 text-gray-500">{a.correctAnswers}/{a.totalQuestions}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
