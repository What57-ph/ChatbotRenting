import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Zap, CheckCircle2 } from "lucide-react"

export function CurrentPlan() {
  return (
    <Card className="bg-zinc-900 border-zinc-800">
      <CardHeader className="pb-4 border-b border-zinc-800/50">
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="text-xl text-zinc-100 flex items-center gap-2">
              Lumina Pro Plan
              <Badge className="bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">Active</Badge>
            </CardTitle>
            <CardDescription className="text-zinc-400 mt-1.5">
              You are currently on the Pro plan. Billed at $29.00/month.
            </CardDescription>
          </div>
          <div className="text-right">
            <span className="text-3xl font-bold text-white">$29</span>
            <span className="text-zinc-500">/mo</span>
          </div>
        </div>
      </CardHeader>
      
      <CardContent className="pt-6 grid md:grid-cols-2 gap-8">
        <div className="space-y-4">
          <h4 className="text-sm font-medium text-zinc-300 uppercase tracking-wider">Plan Limits & Usage</h4>
          
          <div className="space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-zinc-400 flex items-center"><Zap className="w-3.5 h-3.5 mr-1.5 text-amber-500" /> API Tokens</span>
              <span className="text-zinc-300 font-medium">1.2M / 2.0M</span>
            </div>
            <div className="h-2 w-full bg-zinc-800 rounded-full overflow-hidden">
              <div className="h-full bg-amber-500 w-[60%] rounded-full shadow-[0_0_10px_rgba(245,158,11,0.5)]" />
            </div>
          </div>
          
          <div className="space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-zinc-400 flex items-center">Active Bots</span>
              <span className="text-zinc-300 font-medium">8 / 10</span>
            </div>
            <div className="h-2 w-full bg-zinc-800 rounded-full overflow-hidden">
              <div className="h-full bg-emerald-500 w-[80%] rounded-full shadow-[0_0_10px_rgba(16,185,129,0.5)]" />
            </div>
          </div>
        </div>

        <div className="space-y-4">
          <h4 className="text-sm font-medium text-zinc-300 uppercase tracking-wider">Plan Features</h4>
          <ul className="space-y-2.5">
            {["Up to 10 active Chatbots", "Priority Claude 3.5 & GPT-4o access", "Advanced Model Fine-tuning", "Custom Webhook integrations"].map((feature, i) => (
              <li key={i} className="flex items-start text-sm text-zinc-400">
                <CheckCircle2 className="w-4 h-4 mr-2 text-indigo-400 shrink-0 mt-0.5" />
                {feature}
              </li>
            ))}
          </ul>
        </div>
      </CardContent>

      <CardFooter className="pt-4 pb-6 bg-zinc-900/50 border-t border-zinc-800 flex justify-between items-center px-6">
        <p className="text-sm text-zinc-500">Your plan renews on <span className="text-zinc-300 font-medium">August 24, 2026</span>.</p>
        <div className="flex gap-3">
          <Button variant="outline" className="bg-zinc-900 border-zinc-700 text-zinc-300 hover:bg-zinc-800">
            Cancel Subscription
          </Button>
          <Button className="bg-white text-zinc-900 hover:bg-zinc-200 font-medium">
            Upgrade to Enterprise
          </Button>
        </div>
      </CardFooter>
    </Card>
  )
}
