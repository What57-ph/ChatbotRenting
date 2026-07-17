"use client"

import { useState, useEffect } from "react"
import { usePathname } from "next/navigation"
import { useAppSelector } from "@/redux/store"
import { Bell, Search, Menu, X } from "lucide-react"
import { Avatar } from "@/components/ui/avatar"
import { Sidebar } from "@/components/layout/sidebar"

export function Header() {
  const user = useAppSelector((state) => state.auth.user)
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)
  const pathname = usePathname()

  // Mồi sự kiện: Đóng Drawer mỗi khi URL nhảy hướng (Chuyển trang thành công)
  useEffect(() => {
    setIsMobileMenuOpen(false)
  }, [pathname])

  // Khóa cuộn trang màn hình sau khi bung Overlay Toolbar
  useEffect(() => {
    if (isMobileMenuOpen) {
      document.body.style.overflow = "hidden"
    } else {
      document.body.style.overflow = ""
    }
    return () => { document.body.style.overflow = "" }
  }, [isMobileMenuOpen])

  return (
    <>
      <header className="flex h-16 shrink-0 items-center justify-between border-b border-border/50 bg-background/50 backdrop-blur-xl px-4 sm:px-6">
        
        {/* Nav Trigger & Search Box */}
        <div className="flex items-center gap-3 flex-1">
          {/* Hamburger Menu dành riêng cho Mobile size */}
          <button 
            className="md:hidden p-2 -ml-2 text-muted-foreground hover:text-white hover:bg-zinc-800 rounded-md transition-colors"
            onClick={() => setIsMobileMenuOpen(true)}
          >
            <Menu className="h-5 w-5" />
          </button>

          {/* Ô tìm kiếm sẽ bị giấu trên mobile, nhường chỗ bằng nút kính lúp */}
          <div className="relative w-full max-w-md hidden sm:block">
            <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <input
              type="search"
              placeholder="Search chatbots, transactions..."
              className="h-9 w-full rounded-md border border-border/50 bg-muted/30 px-9 py-2 text-sm text-foreground shadow-sm transition-colors placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-primary"
            />
          </div>
        </div>

        {/* Khối tài khoản & Thông báo */}
        <div className="flex items-center gap-2 sm:gap-6">
          <button className="sm:hidden p-2 text-muted-foreground hover:bg-muted/50 hover:text-white transition-colors rounded-full">
            <Search className="h-5 w-5" />
          </button>

          <button className="relative rounded-full p-2 text-muted-foreground hover:bg-muted/50 hover:text-white transition-colors">
            <Bell className="h-5 w-5" />
            <span className="absolute right-1.5 top-1.5 flex h-2 w-2 rounded-full bg-indigo-500 border border-background shadow-[0_0_8px_rgba(99,102,241,0.8)]"></span>
          </button>

          <div className="flex items-center gap-3 border-l border-border/50 pl-3 sm:pl-6 ml-1 sm:ml-0">
            <div className="hidden sm:flex flex-col items-end">
              <span className="text-sm font-medium leading-none text-zinc-200">{user?.username || "Admin"}</span>
              <span className="text-xs text-muted-foreground mt-1">{user?.role || "System Operator"}</span>
            </div>
            <Avatar alt={user?.username || "AD"} status="online" />
          </div>
        </div>
      </header>

      {/* Slide-over Overlay cho Mobile Sidebar */}
      {isMobileMenuOpen && (
        <div className="fixed inset-0 z-50 md:hidden flex">
          {/* Glass Overlay Background đằng sau */}
          <div 
            className="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity"
            onClick={() => setIsMobileMenuOpen(false)}
          />
          
          {/* Menu Drawer trượt từ mép màn hình Trái sang */}
          <div className="relative w-64 h-full bg-background flex-shrink-0 animate-in slide-in-from-left duration-300 shadow-2xl flex flex-col">
            <button 
              className="absolute top-4 right-4 p-2 text-zinc-400 hover:text-white hover:bg-zinc-800 rounded-md transition-colors z-50 bg-background/80 backdrop-blur-sm"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              <X className="w-5 h-5" />
            </button>
            <div className="h-full w-full relative">
              <Sidebar />
            </div>
          </div>
        </div>
      )}
    </>
  )
}
