# Bank PLI Frontend

Standalone React frontend for the Bank PLI modernization. It implements the user-facing modules listed in the migration plan:

- Operations overview and system status
- Customer search, creation, credit state, and termination
- Account opening and mortgage schedule calculation
- Transfer entry, fee preview, authorization prompts, routing, and confirmation
- Five-bureau credit scoring and decision display
- SWIFT MT103/MT202 payments, BIC entry, FX conversion, and Unicode beneficiary names
- Read-only audit log and reporting view
- Runtime configuration, KYC validation, and batch generation
- Responsive navigation, API errors, loading states, and user feedback

## Start locally

Run the Java backend on port 8080, then:

```bash
npm install
npm run dev
```

Open <http://localhost:5173>. Vite proxies `/api` to `http://localhost:8080`.

## Environment

Copy `.env.example` to `.env.local` when the backend is hosted separately:

```text
VITE_API_BASE_URL=https://api.example.com/api
```

The deployed backend must permit the frontend origin, or both apps should be served behind one reverse proxy.

## Verification

```bash
npm test
npm run build
```

The production output is generated in `dist/`.
