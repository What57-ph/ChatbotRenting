import Link from "next/link"
import { Bot } from "lucide-react"

export function SiteFooter() {
  return (
    <footer className="border-t border-white/10 bg-black py-12 px-6 lg:px-12">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-6">
        <div className="flex items-center gap-2">
          <Bot className="h-6 w-6 text-primary" />
          <span className="font-semibold text-white tracking-tight">Lumina AI</span>
        </div>
        <p className="text-sm text-zinc-500">
          © {new Date().getFullYear()} Lumina Chatbot Renting Platform. All rights reserved.
        </p>
        <div className="flex items-center gap-4 text-sm text-zinc-400">
          <Link href="#" className="hover:text-white transition-colors">Privacy</Link>
          <Link href="#" className="hover:text-white transition-colors">Terms of Service</Link>
          <Link href="#" className="hover:text-white transition-colors">Contact</Link>
        </div>
      </div>
    </footer>
  )
}
