"use client"

import * as React from "react"
import { useState } from "react"
import { Star, StarHalf, MessageSquareQuote, Search, Filter } from "lucide-react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"

// Types
type Review = {
  id: string
  botName: string
  authorName: string
  rating: number
  date: string
  content: string
  isVerified: boolean
  role: "renter" | "creator"
}

// Mock Data
const MOCK_REVIEWS: Review[] = [
  {
    id: "r1",
    botName: "Customer Support Pro",
    authorName: "Alex Morgan",
    rating: 5,
    date: "2 days ago",
    content: "Absolutely phenomenal chatbot. It handled over 5,000 queries during our Black Friday sale without a single hallucination. The integration was seamless.",
    isVerified: true,
    role: "renter"
  },
  {
    id: "r2",
    botName: "Code Assistant GPT-4",
    authorName: "Sarah Chen",
    rating: 4,
    date: "1 week ago",
    content: "Very good at writing Python scripts, but sometimes struggles with Next.js 14 app router specifics. Sill, a huge time saver for our dev team.",
    isVerified: true,
    role: "renter"
  },
  {
    id: "r3",
    botName: "Sales Lead Gen Bot",
    authorName: "John Smith",
    rating: 2,
    date: "3 weeks ago",
    content: "The bot is too aggressive in asking for emails. We saw a spike in bounce rates after deploying it. Needs better prompt instructions.",
    isVerified: false,
    role: "renter"
  },
]

const MOCK_GIVEN_REVIEWS: Review[] = [
  {
    id: "g1",
    botName: "Legal Document Analyzer",
    authorName: "You",
    rating: 5,
    date: "1 month ago",
    content: "Incredible accuracy parsing through dense NDA contracts. Highly recommend this agent for any law firm looking to automate paperwork.",
    isVerified: true,
    role: "creator" // using creator here conceptually for "my given review" in the UI
  }
]

// Render Stars Helper
const StarRating = ({ rating }: { rating: number }) => {
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

export default function ReviewsPage() {
  const [activeTab, setActiveTab] = useState<"received" | "given">("received")
  const currentReviews = activeTab === "received" ? MOCK_REVIEWS : MOCK_GIVEN_REVIEWS

  return (
    <div className="flex flex-col gap-6 w-full pb-8">
      {/* Header Area */}
      <div className="flex flex-col gap-2">
        <h1 className="text-3xl font-bold tracking-tight">Reviews</h1>
        <p className="text-sm text-muted-foreground">
          Manage and track feedback for your AI Chatbot services.
        </p>
      </div>

      {/* Tabs */}
      <div className="flex items-center gap-4 border-b border-border">
        <button
          onClick={() => setActiveTab("received")}
          className={`pb-3 text-sm font-medium transition-colors border-b-2 ${
            activeTab === "received" 
              ? "border-primary text-foreground" 
              : "border-transparent text-muted-foreground hover:text-foreground"
          }`}
        >
          Received Reviews (Creator)
        </button>
        <button
          onClick={() => setActiveTab("given")}
          className={`pb-3 text-sm font-medium transition-colors border-b-2 ${
            activeTab === "given" 
              ? "border-primary text-foreground" 
              : "border-transparent text-muted-foreground hover:text-foreground"
          }`}
        >
          Given Reviews (Renter)
        </button>
      </div>

      {/* Statistic Section (Only for Received) */}
      {activeTab === "received" && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Average Score */}
          <Card className="flex flex-col justify-center items-center p-6 bg-card border-border shadow-sm">
            <h3 className="text-sm font-medium text-muted-foreground mb-2">Average Rating</h3>
            <div className="text-5xl font-bold tracking-tighter text-primary mb-2">4.2</div>
            <StarRating rating={4.2} />
            <p className="text-xs text-muted-foreground mt-4">Based on 142 reviews</p>
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
      )}

      {/* Filters and List */}
      <Card className="border-border shadow-sm">
        <CardHeader className="pb-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <CardTitle className="text-lg flex items-center gap-2">
              <MessageSquareQuote className="h-5 w-5 text-primary" />
              {activeTab === "received" ? "Recent Feedback" : "Your History"}
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
          {currentReviews.map((review) => (
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

          {currentReviews.length === 0 && (
            <div className="py-12 text-center text-muted-foreground flex flex-col items-center">
              <MessageSquareQuote className="h-10 w-10 mb-4 opacity-20" />
              <p>No reviews found.</p>
            </div>
          )}
        </CardContent>
        {currentReviews.length > 0 && (
          <CardFooter className="flex justify-center border-t border-border/50 pt-6">
            <Button variant="ghost" className="text-muted-foreground">Load More Reviews</Button>
          </CardFooter>
        )}
      </Card>
    </div>
  )
}
