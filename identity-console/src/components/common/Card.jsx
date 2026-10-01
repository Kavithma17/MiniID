export default function Card({ title, eyebrow, action, children, className = '' }) {
  return <section className={`panel ${className}`}><div className="panel-heading"><div>{eyebrow && <p className="eyebrow">{eyebrow}</p>}{title && <h2>{title}</h2>}</div>{action}</div>{children}</section>
}
