import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { TrendingUp, type LucideIcon } from "lucide-react"

interface StatItem {
  title: string
  value: string
  description: string
  icon: LucideIcon
  trend: "up" | "down"
}

interface StatsGridProps {
  stats: StatItem[]
}

export function StatsGrid({ stats }: StatsGridProps) {
  return (
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
  )
}
