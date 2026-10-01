import PageContainer from '../components/layout/PageContainer.jsx'
import Card from '../components/common/Card.jsx'
import Button from '../components/common/Button.jsx'

export default function SettingsPage() {
  return <PageContainer><section className="page-heading resource-heading"><div><p className="eyebrow">Workspace</p><h1>Settings</h1><p className="subheading">Control security, authentication, tokens, and branding.</p></div><Button>Save changes</Button></section><div className="settings-grid"><Card title="General" eyebrow="Workspace"><label className="field"><span>Workspace name</span><input defaultValue="Acme workspace" /></label><label className="field"><span>Workspace URL</span><input defaultValue="acme.miniid.dev" /></label></Card><Card title="Security" eyebrow="Protection"><label className="toggle-row"><span><b>Require MFA</b><small>Ask every administrator to use a second factor.</small></span><input type="checkbox" defaultChecked /></label><label className="toggle-row"><span><b>Allow passkeys</b><small>Enable passwordless sign-in for members.</small></span><input type="checkbox" defaultChecked /></label></Card></div></PageContainer>
}
