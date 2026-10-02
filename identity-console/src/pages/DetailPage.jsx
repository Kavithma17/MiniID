import { Link } from 'react-router-dom'
import PageContainer from '../components/layout/PageContainer.jsx'
import Card from '../components/common/Card.jsx'
import Badge from '../components/common/Badge.jsx'
import Icon from '../components/common/Icon.jsx'

export default function DetailPage({ kind = 'user' }) {
  const user = kind === 'user'
  const role = kind === 'role'
  const title = user ? 'Jordan Miller' : role ? 'Support agent' : 'Slack workspace'
  const subtitle = user ? 'jordan.miller@acme.co' : role ? '12 members · 24 permissions' : 'OAuth and OpenID Connect configuration'
  return <PageContainer><Link className="back-link" to={user ? '/users' : role ? '/roles' : '/applications'}><Icon name="back" size={15} />Back to {user ? 'users' : role ? 'roles' : 'applications'}</Link><section className="detail-heading"><div className="detail-avatar">{user ? 'JM' : role ? 'SA' : 'S'}</div><div><p className="eyebrow">{user ? 'User details' : role ? 'Role details' : 'Application details'}</p><h1>{title}</h1><p className="subheading">{subtitle}</p></div><Badge tone="connected">{role ? 'Active' : user ? 'Active' : 'Connected'}</Badge></section><div className="detail-grid"><Card title="Overview" eyebrow="Configuration"><div className="detail-list"><div><span>{user ? 'Username' : role ? 'Role key' : 'Client ID'}</span><b>{user ? 'jordan.miller' : role ? 'support_agent' : 'mini_slack_8f1c'}</b></div><div><span>{user ? 'Created' : role ? 'Members' : 'Protocol'}</span><b>{user ? 'September 28, 2026' : role ? '12 members' : 'OpenID Connect'}</b></div><div><span>{user ? 'Last active' : role ? 'Permissions' : 'Redirect URIs'}</span><b>{user ? '2 minutes ago' : role ? '24 permissions' : '3 configured'}</b></div></div></Card><Card title={user ? 'Credentials' : role ? 'Permissions' : 'Authentication'} eyebrow={user ? 'Security' : role ? 'Access' : 'Flow'}><div className="detail-list"><div><span>{user ? 'Passkey' : role ? 'Access level' : 'Sign-in methods'}</span><Badge tone="connected">{role ? 'Scoped' : 'Configured'}</Badge></div><div><span>{user ? 'Password' : role ? 'Assigned users' : 'Token policy'}</span><b>{user ? 'Enabled' : role ? '12 users' : 'Default policy'}</b></div><div><span>{user ? 'MFA' : role ? 'Approval' : 'Consent screen'}</span><b>{user ? 'Required' : role ? 'Not required' : 'Published'}</b></div></div></Card></div></PageContainer>
}
