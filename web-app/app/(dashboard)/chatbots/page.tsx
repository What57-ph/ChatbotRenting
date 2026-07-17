import { ChatbotStats } from "@/components/ui/chatbots/chatbot-stats"
import { ChatbotGrid } from "@/components/ui/chatbots/chatbot-grid"
import { ChatbotData } from "@/components/ui/chatbots/chatbot-card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"

// Mock Data
const MOCK_CHATBOTS: ChatbotData[] = [
  { id: "1", name: "Nexus Customer Support", description: "Automated standard replies for Tier 1 support. Trained on 2024 docs.", model: "GPT-4o", status: "Active", usagePercentage: 85, totalTokens: "850k", role: "Created" },
  { id: "2", name: "Code Assistant Pro", description: "Expert software engineer bot specialized in React and Node.js ecosystems.", model: "Claude 3.5 Sonnet", status: "Active", usagePercentage: 42, totalTokens: "420k", role: "Rented" },
  { id: "3", name: "Market Analyzer X", description: "Scrapes and evaluates stock market trends based on real-time news sources.", model: "Llama 3 70B", status: "Paused", usagePercentage: 0, totalTokens: "0", role: "Rented" },
  { id: "4", name: "SEO Content Generator", description: "Drafts highly optimized blog posts from keyword inputs and competitor analysis.", model: "GPT-4o", status: "Active", usagePercentage: 92, totalTokens: "920k", role: "Created" },
  { id: "5", name: "Database Admin Buddy", description: "Generates SQL queries, optimizes schema, and explains execution plans.", model: "Claude 3 Opus", status: "Maintenance", usagePercentage: 15, totalTokens: "150k", role: "Rented" },
  { id: "6", name: "Creative Writing Muse", description: "Brainstorms plot ideas, character arcs, and dialogue for fiction writers.", model: "Claude 3.5 Sonnet", status: "Active", usagePercentage: 66, totalTokens: "660k", role: "Rented" },
]

export default function ChatbotsPage() {
  const rentedBots = MOCK_CHATBOTS.filter(bot => bot.role === "Rented")
  const createdBots = MOCK_CHATBOTS.filter(bot => bot.role === "Created")

  return (
    <div className="max-w-6xl mx-auto space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-white mb-1">My Chatbots</h1>
          <p className="text-zinc-400">Manage your active fleet, track usage, and discover new AI models.</p>
        </div>
        <div className="flex gap-3">
          <Button variant="outline" className="bg-zinc-900 border-zinc-800 text-zinc-300 hover:text-white hover:bg-zinc-800">
            Explore Marketplace
          </Button>
          <Button className="bg-indigo-600 hover:bg-indigo-700 text-white">
            <Plus className="w-4 h-4 mr-2" />
            Deploy New Bot
          </Button>
        </div>
      </div>

      {/* Overview Stats */}
      <ChatbotStats />

      {/* Main Content Tabs */}
      <Tabs defaultValue="rented" className="space-y-6">
        <div className="flex justify-between items-center border-b border-zinc-800 pb-px">
          <TabsList className="bg-transparent h-auto p-0 border-b-0 space-x-6">
            <TabsTrigger 
              value="rented" 
              className="data-[state=active]:bg-transparent data-[state=active]:shadow-none data-[state=active]:border-b-2 data-[state=active]:border-indigo-500 data-[state=active]:text-indigo-400 rounded-none px-0 pb-3 font-semibold text-zinc-400 hover:text-zinc-300"
            >
              Rented Bots ({rentedBots.length})
            </TabsTrigger>
            <TabsTrigger 
              value="created" 
              className="data-[state=active]:bg-transparent data-[state=active]:shadow-none data-[state=active]:border-b-2 data-[state=active]:border-indigo-500 data-[state=active]:text-indigo-400 rounded-none px-0 pb-3 font-semibold text-zinc-400 hover:text-zinc-300"
            >
              My Creations ({createdBots.length})
            </TabsTrigger>
          </TabsList>
        </div>

        <TabsContent value="rented" className="mt-0 outline-none">
          <ChatbotGrid chatbots={rentedBots} />
        </TabsContent>

        <TabsContent value="created" className="mt-0 outline-none">
          <ChatbotGrid chatbots={createdBots} />
        </TabsContent>
      </Tabs>
    </div>
  )
}
