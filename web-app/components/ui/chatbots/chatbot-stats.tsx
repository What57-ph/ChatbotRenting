import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Bot, Zap, Activity } from "lucide-react"

export function ChatbotStats() {
  return (
    <div className="grid gap-4 md:grid-cols-3">
      <Card className="bg-zinc-900/50 border-zinc-800">
        <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
          <CardTitle className="text-sm font-medium text-zinc-400">Total Active Bots</CardTitle>
          <Bot className="w-4 h-4 text-emerald-400" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold text-zinc-100">8 / 10</div>
          <p className="text-xs text-zinc-500 mt-1">2 slots available in current plan</p>
        </CardContent>
      </Card>
      
      <Card className="bg-zinc-900/50 border-zinc-800">
        <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
          <CardTitle className="text-sm font-medium text-zinc-400">Total Tokens Consumed</CardTitle>
          <Zap className="w-4 h-4 text-amber-400" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold text-zinc-100">1.2M</div>
          <p className="text-xs text-zinc-500 mt-1">~15% of monthly limit</p>
        </CardContent>
      </Card>
      
      <Card className="bg-zinc-900/50 border-zinc-800">
        <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
          <CardTitle className="text-sm font-medium text-zinc-400">System Health</CardTitle>
          <Activity className="w-4 h-4 text-blue-400" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold text-zinc-100">99.9%</div>
          <p className="text-xs text-zinc-500 mt-1">All engines running smoothly</p>
        </CardContent>
      </Card>
    </div>
  )
}
