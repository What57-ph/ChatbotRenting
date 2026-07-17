import { Sidebar } from "@/components/layout/sidebar"
import { Header } from "@/components/layout/header"

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="flex h-screen w-full overflow-hidden bg-background">
      {/* Sidebar Sidebar ẩn/hiển (Todo: Xử lý responsive sau) */}
      <div className="hidden md:block">
        <Sidebar />
      </div>

      <div className="flex flex-1 flex-col overflow-hidden">
        <Header />
        
        {/* Nội dung chính scroll độc lập */}
        <main className="flex-1 overflow-y-auto w-full">
          <div className="container mx-auto h-full p-4 sm:p-6 lg:p-8">
            {children}
          </div>
        </main>
      </div>
    </div>
  )
}
