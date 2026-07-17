import { Star, StarHalf } from "lucide-react"

export const StarRating = ({ rating }: { rating: number }) => {
  const fullStars = Math.floor(rating)
  const hasHalf = rating % 1 !== 0
  const emptyStars = 5 - Math.ceil(rating)
  
  return (
    <div className="flex items-center gap-0.5">
      {[...Array(fullStars)].map((_, i) => (
        <Star key={`full-${i}`} className="h-4 w-4 fill-amber-500 text-amber-500" />
      ))}
      {hasHalf && <StarHalf className="h-4 w-4 fill-amber-500 text-amber-500" />}
      {[...Array(emptyStars)].map((_, i) => (
        <Star key={`empty-${i}`} className="h-4 w-4 text-muted-foreground" />
      ))}
    </div>
  )
}
