import Link from "next/link"
import { Bot } from "lucide-react"
import { Button } from "@/components/ui/button"

export function SiteHeader() {
  return (
    <nav className="fixed top-0 inset-x-0 z-50 flex items-center justify-between px-6 lg:px-12 py-4 bg-black/50 backdrop-blur-md border-b border-white/5">
      <div className="flex items-center gap-2">
        <Bot className="h-8 w-8 text-primary" />
        <span className="text-xl font-bold tracking-tighter text-white">Lumina AI</span>
      </div>
      <div className="flex items-center gap-4">
        <Link href="/login" className="text-sm font-medium text-zinc-300 hover:text-white transition-colors">
          Sign In
        </Link>
        <Link href="/login">
          <Button className="rounded-full shadow-lg shadow-primary/25 hover:shadow-primary/40 transition-all font-semibold">
            Get Started
          </Button>
        </Link>
      </div>
    </nav>
  )
}
