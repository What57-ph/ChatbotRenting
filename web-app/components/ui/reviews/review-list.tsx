import { MessageSquareQuote, Search, Filter } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { StarRating } from "./star-rating"

export type Review = {
  id: string
  botName: string
  authorName: string
  rating: number
  date: string
  content: string
  isVerified: boolean
  role: "renter" | "creator"
}

interface ReviewListProps {
  title: string
  reviews: Review[]
  activeTab: "received" | "given"
}

export function ReviewList({ title, reviews, activeTab }: ReviewListProps) {
  return (
    <Card className="border-border shadow-sm">
      <CardHeader className="pb-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <CardTitle className="text-lg flex items-center gap-2">
            <MessageSquareQuote className="h-5 w-5 text-primary" />
            {title}
          </CardTitle>
          <div className="flex items-center gap-2">
            <div className="relative">
              <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                type="search"
                placeholder="Search reviews..."
                className="w-full sm:w-[250px] pl-9 bg-background"
              />
            </div>
            <Button variant="outline" size="icon" className="shrink-0">
              <Filter className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardHeader>
      <CardContent className="grid gap-6">
        {reviews.map((review) => (
          <div key={review.id} className="flex flex-col gap-4 p-5 rounded-xl border border-border/60 bg-muted/20">
            <div className="flex items-start justify-between gap-4">
              <div className="flex items-start gap-4">
                <Avatar className="h-10 w-10 border border-border">
                  <AvatarFallback className="bg-primary/10 text-primary font-semibold">
                    {review.authorName.charAt(0)}
                  </AvatarFallback>
                </Avatar>
                <div className="grid gap-1">
                  <div className="flex items-center gap-2">
                    <span className="font-semibold text-sm">{review.authorName}</span>
                    {review.isVerified && (
                      <Badge variant="outline" className="text-[10px] h-5 px-1.5 font-medium border-primary/20 text-primary bg-primary/5">
                        Verified Renter
                      </Badge>
                    )}
                  </div>
                  <div className="text-xs text-muted-foreground">
                    Reviewed <span className="font-medium text-foreground/80">{review.botName}</span> • {review.date}
                  </div>
                </div>
              </div>
              <StarRating rating={review.rating} />
            </div>
            <p className="text-sm text-foreground/90 leading-relaxed pl-14">
              "{review.content}"
            </p>
            {activeTab === "given" && (
              <div className="pl-14 flex items-center gap-2 pt-2">
                <Button variant="outline" size="sm" className="h-8 text-xs">Edit Review</Button>
                <Button variant="ghost" size="sm" className="h-8 text-xs text-destructive hover:text-destructive">Delete</Button>
              </div>
            )}
          </div>
        ))}

        {reviews.length === 0 && (
          <div className="py-12 text-center text-muted-foreground flex flex-col items-center">
            <MessageSquareQuote className="h-10 w-10 mb-4 opacity-20" />
            <p>No reviews found.</p>
          </div>
        )}
      </CardContent>
      {reviews.length > 0 && (
        <CardFooter className="flex justify-center border-t border-border/50 pt-6">
          <Button variant="ghost" className="text-muted-foreground">Load More Reviews</Button>
        </CardFooter>
      )}
    </Card>
  )
}
