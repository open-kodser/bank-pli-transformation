import { afterEach, describe, expect, it, vi } from 'vitest'
import { api, ApiError, json } from './client.js'

describe('API client', () => {
  afterEach(() => vi.unstubAllGlobals())
  it('parses successful JSON responses', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(JSON.stringify({ status:'UP' }), { status:200, headers:{'Content-Type':'application/json'} })))
    await expect(api('/health')).resolves.toEqual({ status:'UP' })
  })
  it('surfaces backend error messages', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(JSON.stringify({ error:'Invalid BIC' }), { status:400, headers:{'Content-Type':'application/json'} })))
    await expect(api('/swift')).rejects.toEqual(expect.objectContaining({ name:'ApiError', message:'Invalid BIC', status:400 }))
  })
  it('builds JSON request options', () => expect(json('POST',{ amount:10 })).toEqual({ method:'POST', body:'{"amount":10}' }))
  it('exports the typed error', () => expect(new ApiError('bad',409).status).toBe(409))
})
