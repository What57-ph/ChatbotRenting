"use client"

import { Bot, CreditCard, Activity, Plus } from "lucide-react"
import { Button } from "@/components/ui/button"

import { StatsGrid } from "@/components/ui/overview/stats-grid"
import { RecentActivityTable } from "@/components/ui/overview/recent-activity-table"
import { PromotionalCard } from "@/components/ui/overview/promotional-card"

const stats = [
  {
    title: "Total Active Bots",
    value: "12",
    description: "+2 from last month",
    icon: Bot,
    trend: "up" as const
  },
  {
    title: "API Calls (This Month)",
    value: "2.4M",
    description: "64% of 5M limit",
    icon: Activity,
    trend: "up" as const
  },
  {
    title: "Estimated Cost",
    value: "$450.00",
    description: "Next billing: Oct 1",
    icon: CreditCard,
    trend: "down" as const
  }
]

const recentBots = [
  { id: "LMA-001", name: "Customer Support L1", model: "GPT-4o", status: "Active", uptime: "99.9%" },
  { id: "LMA-002", name: "Sales Lead Gen", model: "Claude 3.5", status: "Active", uptime: "100%" },
  { id: "LMA-003", name: "Internal HR Bot", model: "Llama 3 70B", status: "Paused", uptime: "N/A" },
  { id: "LMA-004", name: "Tech Docs Helper", model: "GPT-4o Mini", status: "Active", uptime: "99.99%" },
]

export default function OverviewDashboard() {
  return (
    <div className="flex flex-col gap-8 pb-10">
      {/* Page Header */}
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight mb-2">Overview</h1>
          <p className="text-muted-foreground">
            Monitor your chatbot performances and billing summaries.
          </p>
        </div>
        <Button className="gap-2 shadow-lg shadow-primary/20">
          <Plus className="h-4 w-4" />
          Rent New Chatbot
        </Button>
      </div>

      {/* Stats Grid */}
      <StatsGrid stats={stats} />

      {/* Main Content Area */}
      <div className="grid gap-6 md:grid-cols-7">
        <RecentActivityTable bots={recentBots} />
        <PromotionalCard />
      </div>
    </div>
  )
}
