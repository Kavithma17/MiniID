import { Link, useLocation } from 'react-router-dom'
import Icon from '../common/Icon.jsx'

export default function Header() {
  const location = useLocation()
  const pageName = location.pathname === '/' ? 'Overview' : location.pathname.split('/')[1].replaceAll('-', ' ')
  return <header className="topbar"><div className="breadcrumbs"><span>Workspace</span><b>/</b><strong>{pageName}</strong></div><div className="top-actions"><label className="search"><Icon name="search" size={17} /><input aria-label="Search MiniID" placeholder="Search anything" /></label><button className="icon-button" aria-label="Notifications"><Icon name="bell" size={19} /><i /></button><Link className="top-avatar" to="/settings">JD</Link></div></header>
}
