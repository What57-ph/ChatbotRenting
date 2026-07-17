import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Bot } from "lucide-react"

export function PromotionalCard() {
  return (
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
  )
}
