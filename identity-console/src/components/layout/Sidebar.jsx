import { NavLink } from 'react-router-dom'
import Icon from '../common/Icon.jsx'

const groups = [
  { label: 'Workspace', items: [{ label: 'Overview', to: '/', icon: 'grid' }] },
  { label: 'Identity', items: [{ label: 'Users', to: '/users', icon: 'users', count: '2,840' }, { label: 'Roles', to: '/roles', icon: 'roles' }, { label: 'Organizations', to: '/organizations', icon: 'building', later: true }] },
  { label: 'Access', items: [{ label: 'Applications', to: '/applications', icon: 'app', count: '12' }, { label: 'Identity providers', to: '/identity-providers', icon: 'providers', later: true }] },
  { label: 'Monitoring', items: [{ label: 'Sessions', to: '/sessions', icon: 'sessions', later: true }, { label: 'Audit logs', to: '/audit', icon: 'audit', later: true }] },
]

export default function Sidebar() {
  return <aside className="sidebar"><div className="brand"><span className="brand-mark">M</span><span>mini<span>ID</span></span></div><div className="workspace-switcher"><span className="workspace-dot" /><span><b>Acme workspace</b><small>Personal workspace</small></span><span className="chevron">⌄</span></div>{groups.map((group) => <div className="nav-group" key={group.label}><p className="nav-label">{group.label}</p><nav>{group.items.map((item) => <NavLink end={item.to === '/'} className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} to={item.to} key={item.to}><Icon name={item.icon} /><span>{item.label}</span>{item.count && <em>{item.count}</em>}{item.later && <small>Later</small>}</NavLink>)}</nav></div>)}<div className="sidebar-bottom"><div className="help-card"><span className="help-icon">?</span><div><b>Need a hand?</b><small>Read the documentation</small></div><Icon name="arrow" size={15} /></div><NavLink className="profile" to="/settings"><span className="avatar avatar-dark">JD</span><span><b>Jamie Doe</b><small>Administrator</small></span><span className="dots">•••</span></NavLink></div></aside>
}
