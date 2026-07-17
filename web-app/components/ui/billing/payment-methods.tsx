import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Plus, CreditCard, MoreHorizontal } from "lucide-react"

export function PaymentMethods() {
  return (
    <Card className="bg-zinc-900 border-zinc-800 h-full flex flex-col">
      <CardHeader>
        <div className="flex items-center justify-between">
          <div>
            <CardTitle className="text-lg text-white font-semibold">Payment Methods</CardTitle>
            <CardDescription className="text-zinc-400 mt-1">Manage your billing cards.</CardDescription>
          </div>
          <Button size="icon" variant="outline" className="bg-zinc-800/50 border-zinc-700 text-zinc-300 hover:bg-zinc-800 w-8 h-8 rounded-full">
            <Plus className="w-4 h-4" />
          </Button>
        </div>
      </CardHeader>

      <CardContent className="flex-1 flex flex-col justify-center gap-6">
        
        {/* Virtual Credit Card UI */}
        <div className="relative group perspective-1000">
          <div className="w-full max-w-[320px] mx-auto h-[190px] rounded-2xl p-6 flex flex-col justify-between 
            bg-gradient-to-br from-indigo-600 via-purple-600 to-pink-500 
            shadow-[0_10px_40px_-10px_rgba(124,58,237,0.5)] 
            border border-white/20 backdrop-blur-xl transition-transform duration-500 group-hover:scale-105 group-hover:-rotate-1 relative overflow-hidden">
            
            {/* Card Glare Effect */}
            <div className="absolute inset-0 bg-gradient-to-tr from-white/0 via-white/20 to-white/0 opacity-0 group-hover:opacity-100 transition-opacity duration-700 transform -translate-x-full group-hover:translate-x-full" />

            {/* Top Row */}
            <div className="flex justify-between items-center z-10 relative">
              <svg viewBox="0 0 48 48" className="h-8 w-8 opacity-80" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M44 24C44 35.0457 35.0457 44 24 44C12.9543 44 4 35.0457 4 24C4 12.9543 12.9543 4 24 4" stroke="white" strokeWidth="4" strokeLinecap="round"/>
                <circle cx="24" cy="24" r="10" stroke="white" strokeWidth="4"/>
              </svg>
              <div className="font-mono text-white/90 text-sm italic font-semibold tracking-wider">
                VISA
              </div>
            </div>

            {/* Middle Row (Chip) */}
            <div className="z-10 relative mt-2">
              <div className="w-10 h-8 rounded bg-yellow-400/80 border border-yellow-300/50 flex flex-col justify-between p-1 opacity-80">
                <div className="w-full h-px bg-yellow-600/50"></div>
                <div className="w-full h-px bg-yellow-600/50"></div>
                <div className="w-full h-px bg-yellow-600/50"></div>
              </div>
            </div>

            {/* Bottom Row */}
            <div className="z-10 relative mt-4 text-white">
              <div className="font-mono text-xl tracking-[0.2em] mb-1 drop-shadow-md">
                •••• •••• •••• 4242
              </div>
              <div className="flex justify-between text-xs text-white/80 font-medium uppercase tracking-wider mt-2">
                <span>Nguyen Van A</span>
                <span>12/28</span>
              </div>
            </div>
            
          </div>
        </div>

        {/* Status / Selected State */}
        <div className="max-w-[320px] mx-auto w-full flex items-center justify-between text-sm px-2">
          <div className="flex items-center text-emerald-400 mt-2">
            <span className="w-2 h-2 rounded-full bg-emerald-500 mr-2 shadow-[0_0_5px_rgba(16,185,129,0.8)]"></span>
            Primary Payment Card
          </div>
          <Button variant="ghost" size="sm" className="h-6 text-zinc-500 hover:text-white px-2 mt-2">
            Edit
          </Button>
        </div>

      </CardContent>
    </Card>
  )
}
