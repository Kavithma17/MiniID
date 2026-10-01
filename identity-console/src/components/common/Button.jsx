import Icon from './Icon.jsx'

export default function Button({ children, icon, variant = 'primary', ...props }) {
  return <button className={`button button-${variant}`} {...props}>{icon && <Icon name={icon} size={16} />}{children}</button>
}
