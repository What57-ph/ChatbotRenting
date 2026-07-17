"use client"

import { Bot, CreditCard, Activity, TrendingUp, Plus } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

const stats = [
  {
    title: "Total Active Bots",
    value: "12",
    description: "+2 from last month",
    icon: Bot,
    trend: "up"
  },
  {
    title: "API Calls (This Month)",
    value: "2.4M",
    description: "64% of 5M limit",
    icon: Activity,
    trend: "up"
  },
  {
    title: "Estimated Cost",
    value: "$450.00",
    description: "Next billing: Oct 1",
    icon: CreditCard,
    trend: "down"
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
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {stats.map((stat, i) => (
          <Card key={i} className="bg-background/50 backdrop-blur border-border/50 shadow-sm relative overflow-hidden group">
            <div className="absolute inset-x-0 -bottom-px h-px bg-gradient-to-r from-transparent via-primary/50 to-transparent opacity-0 transition-opacity group-hover:opacity-100" />
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                {stat.title}
              </CardTitle>
              <stat.icon className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold">{stat.value}</div>
              <p className="text-xs text-muted-foreground mt-1 flex items-center gap-1">
                {stat.trend === "up" ? (
                  <TrendingUp className="h-3 w-3 text-emerald-500" />
                ) : (
                  <TrendingUp className="h-3 w-3 text-rose-500 rotate-180" />
                )}
                <span className={stat.trend === "up" ? "text-emerald-500" : "text-rose-500"}>
                  {stat.description.split(" ")[0]}
                </span>{" "}
                {stat.description.split(" ").slice(1).join(" ")}
              </p>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Main Content Area */}
      <div className="grid gap-6 md:grid-cols-7">
        <Card className="md:col-span-4 lg:col-span-5 bg-background/50 backdrop-blur border-border/50 shadow-sm">
          <CardHeader>
            <CardTitle>Recent Fleet Activity</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow className="hover:bg-transparent">
                  <TableHead>Instance ID</TableHead>
                  <TableHead>Name</TableHead>
                  <TableHead>Model</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Uptime</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {recentBots.map((bot) => (
                  <TableRow key={bot.id}>
                    <TableCell className="font-mono text-xs text-muted-foreground">{bot.id}</TableCell>
                    <TableCell className="font-medium">{bot.name}</TableCell>
                    <TableCell>{bot.model}</TableCell>
                    <TableCell>
                      <Badge variant={bot.status === "Active" ? "success" : "secondary"}>
                        {bot.status}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-right">{bot.uptime}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>

        <Card className="md:col-span-3 lg:col-span-2 bg-background/50 backdrop-blur border-border/50 shadow-sm flex flex-col items-center justify-center text-center p-6 border-dashed">
          <div className="rounded-full bg-primary/10 p-4 mb-4">
            <Bot className="h-8 w-8 text-primary" />
          </div>
          <h3 className="font-semibold mb-2">Need a specialized agent?</h3>
          <p className="text-sm text-muted-foreground mb-6">
            Deploy a pre-trained model tailored for your exact industry needs in just a few clicks.
          </p>
          <Button variant="outline" className="w-full">Explore Catalog</Button>
        </Card>
      </div>
    </div>
  )
}
