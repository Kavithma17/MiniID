export default function Badge({ children, tone = 'neutral' }) {
  return <span className={`status ${tone}`}>{children}</span>
}
