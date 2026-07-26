import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Download } from "lucide-react"

const MOCK_INVOICES = [
  { id: "INV-2026-004", date: "Jul 24, 2026", amount: "$29.00", status: "Paid", plan: "Pro Plan" },
  { id: "INV-2026-003", date: "Jun 24, 2026", amount: "$29.00", status: "Paid", plan: "Pro Plan" },
  { id: "INV-2026-002", date: "May 24, 2026", amount: "$29.00", status: "Paid", plan: "Pro Plan" },
  { id: "INV-2026-001", date: "Apr 24, 2026", amount: "$0.00", status: "Paid", plan: "Free Trial" },
]

export function BillingHistory() {
  return (
    <Card className="bg-zinc-900 border-zinc-800 h-full flex flex-col">
      <CardHeader>
        <CardTitle className="text-lg text-white font-semibold flex items-center justify-between">
          Billing History
        </CardTitle>
        <CardDescription className="text-zinc-400">View and download your past invoices.</CardDescription>
      </CardHeader>
      
      <CardContent className="flex-1">
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="text-xs text-zinc-500 uppercase bg-zinc-900/50 border-b border-zinc-800">
              <tr>
                <th className="px-4 py-3 font-medium">Invoice</th>
                <th className="px-4 py-3 font-medium">Amount</th>
                <th className="px-4 py-3 font-medium">Date</th>
                <th className="px-4 py-3 font-medium">Status</th>
                <th className="px-4 py-3 font-medium text-right">Receipt</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-800">
              {MOCK_INVOICES.map((inv) => (
                <tr key={inv.id} className="hover:bg-zinc-800/30 transition-colors">
                  <td className="px-4 py-3.5">
                    <div className="font-medium text-zinc-200">{inv.id}</div>
                    <div className="text-xs text-zinc-500">{inv.plan}</div>
                  </td>
                  <td className="px-4 py-3.5 font-medium text-zinc-300">{inv.amount}</td>
                  <td className="px-4 py-3.5 text-zinc-400">{inv.date}</td>
                  <td className="px-4 py-3.5">
                    <Badge variant="outline" className={`${inv.status === 'Paid' ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20' : 'bg-zinc-800 text-zinc-400'} rounded-md font-normal`}>
                      {inv.status}
                    </Badge>
                  </td>
                  <td className="px-4 py-3.5 text-right">
                    <button className="text-zinc-400 hover:text-white transition-colors p-2 rounded-md hover:bg-zinc-800 inline-flex">
                      <Download className="w-4 h-4" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  )
}
