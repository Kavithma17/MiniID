import { Link } from 'react-router-dom'
import Card from '../components/common/Card.jsx'
import Button from '../components/common/Button.jsx'
import Badge from '../components/common/Badge.jsx'
import Icon from '../components/common/Icon.jsx'
import PageContainer from '../components/layout/PageContainer.jsx'

const activity = [
  ['JM', 'Jordan Miller', 'signed in with Passkey', '2 min ago', 'blue'],
  ['AK', 'Ava Kim', 'created an account', '18 min ago', 'violet'],
  ['RS', 'Ravi Shah', 'updated security settings', '42 min ago', 'gold'],
  ['LW', 'Lena Wu', 'signed in with Google', '1 hr ago', 'green'],
]

function Metric({ label, value, detail, icon, tone }) {
  return <article className={`metric-card ${tone}`}><div className="metric-top"><span className="metric-icon"><Icon name={icon} size={17} /></span><span className="metric-trend">Healthy</span></div><p>{label}</p><strong>{value}</strong><small>{detail}</small></article>
}

export default function DashboardPage() {
  return <PageContainer><section className="page-heading"><div><p className="eyebrow">Wednesday, October 1, 2026</p><h1>Good morning, Jamie <span>✦</span></h1><p className="subheading">Here is what is happening across your identity workspace.</p></div><Button icon="plus" onClick={() => window.location.assign('/applications/new')}>Add application</Button></section><section className="metrics"><Metric label="Total users" value="2,840" detail="↑ 12.8% from last month" icon="users" tone="blue" /><Metric label="Sign-ins this month" value="18.6k" detail="↑ 8.4% from last month" icon="sessions" tone="orange" /><Metric label="Active applications" value="12" detail="2 awaiting configuration" icon="app" tone="lilac" /><Metric label="Security score" value="94%" detail="Excellent workspace health" icon="providers" tone="green" /></section><section className="dashboard-grid"><Card className="activity-panel" eyebrow="Live feed" title="Recent activity" action={<Link className="text-button" to="/audit">View all <Icon name="arrow" size={15} /></Link>}><div className="activity-list">{activity.map(([initials, name, action, time, tone]) => <div className="activity-row" key={name}><span className={`avatar avatar-${tone}`}>{initials}</span><div className="activity-copy"><b>{name}</b><span>{action}</span></div><time>{time}</time></div>)}</div></Card><Card className="health-panel" eyebrow="Workspace health" title="Security overview" action={<button className="more-button" aria-label="More security options">•••</button>}><div className="score-wrap"><div className="score-ring"><div><strong>94</strong><span>/100</span></div></div><div><b>Excellent</b><p>Your workspace is well protected.</p></div></div><div className="health-items"><div><span className="check">✓</span><span>MFA enabled</span><b>100%</b></div><div><span className="check">✓</span><span>Passkeys registered</span><b>68%</b></div><div><span className="check">✓</span><span>Recovery methods</span><b>92%</b></div></div></Card></section><Card className="applications-panel" eyebrow="Connected services" title="Applications" action={<Link className="text-button" to="/applications">Manage applications <Icon name="arrow" size={15} /></Link>}><div className="application-list">{[['S', 'slack', 'Slack workspace', 'OIDC · Last used 4 min ago', 'Connected', 'connected'], ['N', 'notion', 'Notion', 'OAuth 2.0 · Last used yesterday', 'Connected', 'connected'], ['L', 'linear', 'Linear', 'OIDC · Setup incomplete', 'Needs setup', 'pending']].map(([mark, tone, name, detail, status, statusTone]) => <Link className="application-row" to="/applications" key={name}><span className={`app-logo ${tone}`}>{mark}</span><span><b>{name}</b><small>{detail}</small></span><Badge tone={statusTone}>{status}</Badge><Icon name="arrow" size={16} /></Link>)}</div></Card></PageContainer>
}
