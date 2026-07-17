"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { LayoutDashboard, MessageSquare, CreditCard, Settings, LogOut, Bot, Star } from "lucide-react"

import { cn } from "@/lib/utils"

const navigation = [
  { name: "Overview", href: "/overview", icon: LayoutDashboard },
  { name: "My Chatbots", href: "/chatbots", icon: MessageSquare },
  { name: "Reviews", href: "/reviews", icon: Star },
  { name: "Billing", href: "/billing", icon: CreditCard },
  { name: "Settings", href: "/settings", icon: Settings },
]

export function Sidebar() {
  const pathname = usePathname()

  return (
    <div className="flex h-full w-64 flex-col bg-background/50 backdrop-blur-xl border-r border-border/50">
      <div className="flex h-16 items-center px-6 border-b border-border/50">
        <Link href="/" className="flex items-center gap-2 text-primary font-bold text-xl tracking-tight">
          <Bot className="h-6 w-6 text-primary" />
          <span>Lumina AI</span>
        </Link>
      </div>

      <div className="flex-1 overflow-y-auto py-6 px-4">
        <nav className="flex flex-col gap-1.5">
          {navigation.map((item) => {
            const isActive = pathname === item.href
            const Icon = item.icon
            return (
              <Link
                key={item.name}
                href={item.href}
                className={cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all",
                  isActive
                    ? "bg-primary/10 text-primary"
                    : "text-muted-foreground hover:bg-muted/50 hover:text-foreground"
                )}
              >
                <Icon className={cn("h-4 w-4", isActive ? "text-primary" : "text-muted-foreground")} />
                {item.name}
              </Link>
            )
          })}
        </nav>
      </div>

      <div className="p-4 border-t border-border/50">
        <Link
          href="/login"
          className="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-muted-foreground hover:bg-destructive/10 hover:text-destructive transition-all"
        >
          <LogOut className="h-4 w-4" />
          Logout
        </Link>
      </div>
    </div>
  )
}
