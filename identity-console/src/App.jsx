import { BrowserRouter } from 'react-router-dom'
import AppShell from './components/layout/AppShell.jsx'
import AppRoutes from './routes/AppRoutes.jsx'
import './App.css'

export default function App() {
  return <BrowserRouter><AppShell><AppRoutes /></AppShell></BrowserRouter>
}
