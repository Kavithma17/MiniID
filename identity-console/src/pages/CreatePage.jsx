import { Link } from 'react-router-dom'
import PageContainer from '../components/layout/PageContainer.jsx'
import Button from '../components/common/Button.jsx'
import Icon from '../components/common/Icon.jsx'

export default function CreatePage({ kind }) {
  const application = kind === 'application'
  const role = kind === 'role'
  const title = application ? 'Create application' : role ? 'Create role' : 'Create user'
  const eyebrow = application ? 'Access' : 'Identity'
  const description = application ? 'Register a new OAuth or OpenID Connect client.' : role ? 'Define a reusable set of permissions for your team.' : 'Add a member to your identity workspace.'
  return <PageContainer><Link className="back-link" to={application ? '/applications' : role ? '/roles' : '/users'}><Icon name="back" size={15} />Back</Link><section className="page-heading resource-heading"><div><p className="eyebrow">{eyebrow}</p><h1>{title}</h1><p className="subheading">{description}</p></div></section><section className="form-panel"><label className="field"><span>{application ? 'Application name' : role ? 'Role name' : 'Full name'}</span><input placeholder={application ? 'e.g. Customer portal' : role ? 'e.g. Support agent' : 'e.g. Jamie Doe'} /></label><label className="field"><span>{application ? 'Protocol' : role ? 'Description' : 'Email address'}</span>{application ? <select defaultValue="OIDC"><option>OIDC</option><option>OAuth 2.0</option></select> : role ? <input placeholder="What can this role access?" /> : <input placeholder="name@company.com" type="email" />}</label><div className="form-actions"><Link className="button button-secondary" to={application ? '/applications' : role ? '/roles' : '/users'}>Cancel</Link><Button icon="plus">{title}</Button></div></section></PageContainer>
}
