import { useEffect, useState } from 'react'
import { api } from './api/client.js'
import { Icon } from './components/Icons.jsx'
import Toast from './components/Toast.jsx'
import Dashboard from './features/Dashboard.jsx'
import Customers from './features/Customers.jsx'
import Accounts from './features/Accounts.jsx'
import Transfers from './features/Transfers.jsx'
import Credit from './features/Credit.jsx'
import SwiftFx from './features/SwiftFx.jsx'
import Audit from './features/Audit.jsx'
import Admin from './features/Admin.jsx'

const sections = [
  ['dashboard','Overview'],['customers','Customers'],['accounts','Accounts'],['transfers','Transfers'],
  ['credit','Credit'],['swift','SWIFT & FX'],['audit','Audit'],['admin','Administration']
]
const views = { dashboard:Dashboard, customers:Customers, accounts:Accounts, transfers:Transfers, credit:Credit, swift:SwiftFx, audit:Audit, admin:Admin }

export default function App() {
  const initial = location.hash.slice(1) || 'dashboard'
  const [active,setActive] = useState(sections.some(([id])=>id===initial)?initial:'dashboard')
  const [online,setOnline] = useState(null)
  const [toast,setToast] = useState(null)
  const [mobile,setMobile] = useState(false)
  useEffect(()=>{api('/health').then(()=>setOnline(true)).catch(()=>setOnline(false))},[])
  const navigate=id=>{setActive(id);location.hash=id;setMobile(false)}
  const View=views[active]
  return <div className="shell">
    <aside className={mobile?'open':''}>
      <div className="brand"><span>BP</span><div><strong>Bank PLI</strong><small>Operations portal</small></div></div>
      <nav>{sections.map(([id,label])=><button className={active===id?'active':''} onClick={()=>navigate(id)} key={id}><Icon name={id}/><span>{label}</span></button>)}</nav>
      <div className="system-state"><i className={online?'online':online===false?'offline':''}/><div><strong>{online?'Systems operational':online===false?'Backend unavailable':'Checking systems'}</strong><small>Bank PLI API</small></div></div>
    </aside>
    <main className="workspace">
      <header className="topbar"><button className="menu" onClick={()=>setMobile(!mobile)}>☰</button><div><p className="eyebrow">Banking operations</p><h1>{sections.find(([id])=>id===active)[1]}</h1></div><div className="operator"><span>PD</span><div><strong>Operator</strong><small>Secure session</small></div></div></header>
      <div className="content"><View notify={(message,type='success')=>setToast({message,type})}/></div>
    </main>
    <Toast toast={toast} onClose={()=>setToast(null)}/>
  </div>
}
