import Sidebar from './Sidebar.jsx'
import Header from './Header.jsx'

export default function AppShell({ children }) {
  return <div className="app-shell"><Sidebar /><main className="main-content"><Header />{children}</main></div>
}
