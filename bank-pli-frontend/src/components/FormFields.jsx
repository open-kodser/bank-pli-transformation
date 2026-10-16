export function Field({ label, hint, ...props }) {
  return <label className="field"><span>{label}</span><input {...props}/>{hint && <small>{hint}</small>}</label>
}
export function Select({ label, children, ...props }) {
  return <label className="field"><span>{label}</span><select {...props}>{children}</select></label>
}
export function Card({ title, eyebrow, actions, children, className='' }) {
  return <section className={`card ${className}`}><header className="card-head"><div>{eyebrow&&<p className="eyebrow">{eyebrow}</p>}<h2>{title}</h2></div>{actions}</header>{children}</section>
}
export function Status({ tone='neutral', children }) { return <span className={`status ${tone}`}>{children}</span> }
export function Empty({ children='No records found.' }) { return <div className="empty">{children}</div> }
