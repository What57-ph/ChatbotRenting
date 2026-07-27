import { BotMessageSquare, Sparkles } from "lucide-react"

export function AuthBranding() {
  return (
    <div className="hidden md:flex flex-col justify-center items-center bg-zinc-900 bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-indigo-900 via-zinc-900 to-black text-white p-12 relative overflow-hidden h-full min-h-screen">
      {/* Glow effect */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-indigo-500/20 blur-[100px] rounded-full" />
      
      <div className="z-10 text-center max-w-md">
        <div className="flex items-center justify-center space-x-3 mb-6">
          <BotMessageSquare className="w-12 h-12 text-indigo-400" />
          <h1 className="text-4xl font-bold tracking-tight text-white">Lumina AI</h1>
        </div>
        <p className="text-lg text-zinc-400 mb-8 leading-relaxed">
          Empower your business with next-generation conversational AI. Build, rent, and scale intelligent chatbots seamlessly.
        </p>
        <div className="inline-flex items-center space-x-2 bg-white/5 border border-white/10 rounded-full px-4 py-2 text-sm text-zinc-300 backdrop-blur-sm">
          <Sparkles className="w-4 h-4 text-amber-400" />
          <span>Premium SaaS Platform</span>
        </div>
      </div>
    </div>
  )
}
