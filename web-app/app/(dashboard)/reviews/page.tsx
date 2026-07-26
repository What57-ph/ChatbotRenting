"use client"

import * as React from "react"
import { useState } from "react"
import { RatingBreakdown } from "@/components/ui/reviews/rating-breakdown"
import { ReviewList, type Review } from "@/components/ui/reviews/review-list"

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
        <RatingBreakdown averageRating={4.2} totalReviews={142} />
      )}

      {/* Filters and List */}
      <ReviewList 
        title={activeTab === "received" ? "Recent Feedback" : "Your History"} 
        reviews={currentReviews} 
        activeTab={activeTab} 
      />
    </div>
  )
}
