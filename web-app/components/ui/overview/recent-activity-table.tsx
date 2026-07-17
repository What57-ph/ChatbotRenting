import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

export interface BotActivity {
  id: string
  name: string
  model: string
  status: string
  uptime: string
}

export function RecentActivityTable({ bots }: { bots: BotActivity[] }) {
  return (
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
            {bots.map((bot) => (
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
  )
}
