"use client"

import { useState } from "react"
import { ChatbotCard, ChatbotData } from "./chatbot-card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Search, LayoutGrid, List as ListIcon, Filter } from "lucide-react"

interface ChatbotGridProps {
  chatbots: ChatbotData[]
}

export function ChatbotGrid({ chatbots }: ChatbotGridProps) {
  const [viewMode, setViewMode] = useState<"grid" | "list">("grid")
  const [searchQuery, setSearchQuery] = useState("")

  const filteredChatbots = chatbots.filter(bot => 
    bot.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    bot.model.toLowerCase().includes(searchQuery.toLowerCase())
  )

  return (
    <div className="space-y-6">
      {/* Control Bar */}
      <div className="flex flex-col sm:flex-row gap-4 items-center justify-between">
        <div className="relative w-full sm:w-96">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-500" />
          <Input 
            placeholder="Search chatbots by name or model..." 
            className="pl-9 bg-zinc-900 border-zinc-800 text-zinc-100 placeholder:text-zinc-500 focus-visible:ring-indigo-500 focus-visible:border-indigo-500"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
        
        <div className="flex items-center gap-2 w-full sm:w-auto">
          <Button variant="outline" className="bg-zinc-900 border-zinc-800 text-zinc-300 hover:text-white hover:bg-zinc-800">
            <Filter className="w-4 h-4 mr-2" />
            Filters
          </Button>
          
          <div className="flex bg-zinc-900 border border-zinc-800 rounded-md p-1">
            <Button 
              variant="ghost" 
              size="sm" 
              className={`px-3 py-1.5 h-auto rounded ${viewMode === 'grid' ? 'bg-zinc-800 text-white shadow-sm' : 'text-zinc-500 hover:text-zinc-300'}`}
              onClick={() => setViewMode('grid')}
            >
              <LayoutGrid className="w-4 h-4" />
            </Button>
            <Button 
              variant="ghost" 
              size="sm" 
              className={`px-3 py-1.5 h-auto rounded ${viewMode === 'list' ? 'bg-zinc-800 text-white shadow-sm' : 'text-zinc-500 hover:text-zinc-300'}`}
              onClick={() => setViewMode('list')}
            >
              <ListIcon className="w-4 h-4" />
            </Button>
          </div>
        </div>
      </div>

      {/* Render Grid or List */}
      {filteredChatbots.length === 0 ? (
        <div className="text-center py-20 bg-zinc-900/30 border border-zinc-800/50 rounded-xl border-dashed">
          <div className="h-12 w-12 rounded-full bg-zinc-800/50 flex items-center justify-center mx-auto mb-3">
            <Search className="w-6 h-6 text-zinc-500" />
          </div>
          <h3 className="text-zinc-300 font-medium">No chatbots found</h3>
          <p className="text-zinc-500 text-sm mt-1">Try adjusting your search query or filters.</p>
        </div>
      ) : (
        <div className={
          viewMode === "grid" 
            ? "grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5" 
            : "flex flex-col gap-3"
        }>
          {filteredChatbots.map((bot) => (
            <ChatbotCard key={bot.id} chatbot={bot} viewMode={viewMode} />
          ))}
        </div>
      )}
    </div>
  )
}
