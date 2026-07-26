import { Card } from "@/components/ui/card"
import { Star } from "lucide-react"
import { StarRating } from "./star-rating"

interface RatingBreakdownProps {
  averageRating: number
  totalReviews: number
}

export function RatingBreakdown({ averageRating, totalReviews }: RatingBreakdownProps) {
  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
      {/* Average Score */}
      <Card className="flex flex-col justify-center items-center p-6 bg-card border-border shadow-sm">
        <h3 className="text-sm font-medium text-muted-foreground mb-2">Average Rating</h3>
        <div className="text-5xl font-bold tracking-tighter text-primary mb-2">{averageRating.toFixed(1)}</div>
        <StarRating rating={averageRating} />
        <p className="text-xs text-muted-foreground mt-4">Based on {totalReviews} reviews</p>
      </Card>

      {/* Rating Breakdown */}
      <Card className="md:col-span-2 p-6 bg-card border-border shadow-sm flex flex-col justify-center gap-3">
        {[
          { stars: 5, percent: 70, color: "bg-emerald-500" },
          { stars: 4, percent: 15, color: "bg-lime-500" },
          { stars: 3, percent: 8, color: "bg-amber-500" },
          { stars: 2, percent: 5, color: "bg-orange-500" },
          { stars: 1, percent: 2, color: "bg-rose-500" },
        ].map((bar) => (
          <div key={bar.stars} className="flex items-center gap-4 text-sm">
            <div className="w-12 text-muted-foreground font-medium flex items-center justify-end gap-1">
              {bar.stars} <Star className="h-3 w-3" />
            </div>
            <div className="flex-1 h-2.5 rounded-full bg-muted overflow-hidden">
              <div 
                className={`h-full ${bar.color}`} 
                style={{ width: `${bar.percent}%` }}
              />
            </div>
            <div className="w-10 text-right text-muted-foreground">{bar.percent}%</div>
          </div>
        ))}
      </Card>
    </div>
  )
}
