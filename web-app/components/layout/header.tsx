"use client"

import { useAppSelector } from "@/redux/store"
import { Bell, Search } from "lucide-react"
import { Avatar } from "@/components/ui/avatar"

export function Header() {
  const user = useAppSelector((state) => state.auth.user)

  return (
    <header className="flex h-16 shrink-0 items-center justify-between border-b border-border/50 bg-background/50 backdrop-blur-xl px-6">
      {/* Khung Search */}
      <div className="flex flex-1 items-center gap-4">
        <div className="relative w-full max-w-md">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
          <input
            type="search"
            placeholder="Search chatbots, transactions..."
            className="h-9 w-full rounded-md border border-border/50 bg-muted/30 px-9 py-2 text-sm text-foreground shadow-sm transition-colors placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-primary"
          />
        </div>
      </div>

      {/* Thông tin người dùng */}
      <div className="flex items-center gap-6">
        <button className="relative rounded-full p-1.5 text-muted-foreground hover:bg-muted/50 hover:text-foreground transition-colors">
          <Bell className="h-5 w-5" />
          <span className="absolute right-1 top-1 flex h-2 w-2 rounded-full bg-destructive"></span>
        </button>

        <div className="flex items-center gap-3 border-l border-border/50 pl-6">
          <div className="flex flex-col items-end">
            <span className="text-sm font-medium leading-none">{user?.username || "Admin"}</span>
            <span className="text-xs text-muted-foreground mt-1">{user?.role || "System Operator"}</span>
          </div>
          <Avatar alt={user?.username || "AD"} status="online" />
        </div>
      </div>
    </header>
  )
}
