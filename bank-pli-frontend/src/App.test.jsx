import { render, screen, waitFor } from '@testing-library/react'
import { afterEach, expect, it, vi } from 'vitest'
import App from './App.jsx'

afterEach(()=>vi.unstubAllGlobals())
it('renders the operations navigation and system state', async()=>{
  vi.stubGlobal('fetch',vi.fn().mockImplementation(url=>Promise.resolve(new Response(JSON.stringify(String(url).endsWith('/audit')?[]:{status:'UP'}),{status:200,headers:{'Content-Type':'application/json'}}))))
  render(<App/>)
  expect(screen.getByText('Bank PLI')).toBeInTheDocument()
  expect(screen.getByText('Customers')).toBeInTheDocument()
  expect(screen.getByText('SWIFT & FX')).toBeInTheDocument()
  await waitFor(()=>expect(screen.getByText('Systems operational')).toBeInTheDocument())
})
