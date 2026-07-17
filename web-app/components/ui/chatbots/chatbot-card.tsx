import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Bot, Play, Pause, Settings, MessageSquare, MoreVertical, Cpu } from "lucide-react"

export type ChatbotData = {
  id: string
  name: string
  description: string
  model: string
  status: "Active" | "Paused" | "Maintenance"
  usagePercentage: number
  totalTokens: string
  role: "Rented" | "Created"
}

interface ChatbotCardProps {
  chatbot: ChatbotData
  viewMode: "grid" | "list"
}

export function ChatbotCard({ chatbot, viewMode }: ChatbotCardProps) {
  const isGrid = viewMode === "grid"

  const statusColor = 
    chatbot.status === "Active" ? "bg-emerald-500/10 text-emerald-500 border-emerald-500/20" :
    chatbot.status === "Paused" ? "bg-amber-500/10 text-amber-500 border-amber-500/20" :
    "bg-red-500/10 text-red-500 border-red-500/20"

  const progressColor = 
    chatbot.usagePercentage > 90 ? "bg-red-500" :
    chatbot.usagePercentage > 75 ? "bg-amber-500" :
    "bg-indigo-500"

  return (
    <Card className={`bg-zinc-900 border-zinc-800 flex overflow-hidden transition-all hover:border-zinc-700 ${isGrid ? 'flex-col' : 'flex-row items-center p-4 gap-6'}`}>
      
      {/* Header & Identity */}
      <div className={`${isGrid ? 'p-5 pb-4' : 'flex-shrink-0 flex items-center gap-4 w-1/4'}`}>
        <div className={`flex items-start gap-3 ${isGrid ? 'mb-3' : ''}`}>
          <div className="h-10 w-10 rounded-lg bg-indigo-500/10 flex items-center justify-center flex-shrink-0 border border-indigo-500/20">
            <Bot className="h-5 w-5 text-indigo-400" />
          </div>
          <div>
            <h3 className="font-semibold text-zinc-100 truncate flex items-center gap-2">
              {chatbot.name}
            </h3>
            {isGrid && <p className="text-xs text-zinc-500 line-clamp-1 mt-0.5">{chatbot.description}</p>}
          </div>
        </div>
        
        {isGrid && (
          <div className="flex items-center gap-2 mt-3">
            <Badge variant="outline" className={statusColor}>
              {chatbot.status}
            </Badge>
            <Badge variant="outline" className="bg-zinc-800 text-zinc-300 border-zinc-700">
              <Cpu className="w-3 h-3 mr-1" />
              {chatbot.model}
            </Badge>
          </div>
        )}
      </div>

      {/* List-only Identity Addon */}
      {!isGrid && (
        <div className="flex items-center gap-3 w-1/5">
          <Badge variant="outline" className={statusColor}>
            {chatbot.status}
          </Badge>
          <Badge variant="outline" className="bg-zinc-800 text-zinc-300 border-zinc-700">
            <Cpu className="w-3 h-3 mr-1" />
            {chatbot.model}
          </Badge>
        </div>
      )}

      {/* Usage Progress */}
      <div className={`${isGrid ? 'px-5 pb-5' : 'flex-1'} flex-col justify-center`}>
        <div className="flex justify-between items-center mb-1.5">
          <span className="text-xs font-medium text-zinc-400">Monthly Usage</span>
          <span className="text-xs font-mono text-zinc-500">{chatbot.usagePercentage}%</span>
        </div>
        <div className="h-1.5 w-full bg-zinc-800 rounded-full overflow-hidden">
          <div 
            className={`h-full rounded-full ${progressColor}`} 
            style={{ width: `${chatbot.usagePercentage}%` }} 
          />
        </div>
        <div className="text-[10px] text-zinc-500 mt-1.5 text-right font-mono">
          {chatbot.totalTokens} Tokens
        </div>
      </div>

      {/* Actions */}
      <CardFooter className={`${isGrid ? 'px-5 py-4 border-t border-zinc-800/50 bg-zinc-900/50' : 'p-0 w-[200px] justify-end gap-2'}`}>
        <div className={`flex w-full ${isGrid ? 'gap-2' : 'justify-end gap-2'}`}>
          <Button variant="default" size="sm" className={`bg-indigo-600 hover:bg-indigo-700 text-white ${isGrid ? 'flex-1' : ''}`}>
            <MessageSquare className="w-4 h-4 mr-1.5" />
            Chat
          </Button>
          <Button variant="outline" size="sm" className="bg-zinc-800 border-zinc-700 text-zinc-300 hover:bg-zinc-700">
            <Settings className="w-4 h-4" />
          </Button>
          <Button variant="ghost" size="sm" className="px-2 text-zinc-400 hover:text-zinc-100">
            <MoreVertical className="w-4 h-4" />
          </Button>
        </div>
      </CardFooter>
      
    </Card>
  )
}
