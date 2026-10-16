import { useEffect, useState } from 'react'
import { api } from '../api/client.js'
import { Card, Status } from '../components/FormFields.jsx'

export default function Dashboard() {
  const [audit,setAudit]=useState([])
  useEffect(()=>{api('/audit').then(data=>setAudit(Array.isArray(data)?data:[])).catch(()=>{})},[])
  const metrics=[['Customer service','Search and lifecycle','Ready'],['Money movement','Validation and atomic posting','Ready'],['Risk decisions','3 of 5 bureau quorum','Ready'],['Global payments','SWIFT and currency exchange','Ready']]
  return <div className="stack">
    <section className="hero"><div><p className="eyebrow">Modernized from Bank-PLI</p><h2>One view across every banking operation.</h2><p>Manage customers, accounts, transfers, credit decisions, international payments, and audit controls from a single workspace.</p></div><div className="hero-mark"><span>37</span><small>migrated features</small></div></section>
    <div className="metric-grid">{metrics.map(([title,copy,status])=><article className="metric" key={title}><span className="metric-line"/><p>{title}</p><strong>{copy}</strong><Status tone="success">{status}</Status></article>)}</div>
    <div className="two-col"><Card title="Recent system activity" eyebrow="Audit trail"><div className="activity">{audit.slice(0,5).map(row=><div key={row.id}><span>{row.eventType.slice(0,1)}</span><div><strong>{row.eventType.replaceAll('_',' ')}</strong><small>{row.details}</small></div><time>{new Date(row.occurredAt).toLocaleTimeString()}</time></div>)}{!audit.length&&<p className="muted">Activity appears after the first operation.</p>}</div></Card><Card title="Control posture" eyebrow="Runtime"><ul className="checklist"><li><span>✓</span>All money uses fixed decimal precision</li><li><span>✓</span>Audit records are append only</li><li><span>✓</span>Transfers roll back atomically</li><li><span>✓</span>Global error mapping enabled</li></ul></Card></div>
  </div>
}
