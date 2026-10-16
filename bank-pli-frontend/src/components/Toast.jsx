export default function Toast({ toast, onClose }) {
  if (!toast) return null
  return <div className={`toast ${toast.type || 'success'}`} role="status"><span>{toast.message}</span><button onClick={onClose} aria-label="Dismiss">×</button></div>
}
