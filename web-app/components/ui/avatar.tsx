"use client"

import * as React from "react"
import { cn } from "@/lib/utils"

const AvatarContext = React.createContext<{
  status?: "online" | "offline" | "away"
}>({})

export interface AvatarProps extends React.HTMLAttributes<HTMLDivElement> {
  src?: string
  alt?: string
  fallback?: string
  status?: "online" | "offline" | "away"
}

export function Avatar({ className, src, alt, fallback, status, ...props }: AvatarProps) {
  const [imageError, setImageError] = React.useState(false)

  return (
    <AvatarContext.Provider value={{ status }}>
      <div
        className={cn(
          "relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full border border-border/50 bg-muted/50 items-center justify-center",
          className
        )}
        {...props}
      >
        {src && !imageError ? (
          <img
            src={src}
            alt={alt || "Avatar"}
            className="aspect-square h-full w-full object-cover"
            onError={() => setImageError(true)}
          />
        ) : (
          <span className="text-sm font-medium uppercase text-muted-foreground">
            {fallback || (alt ? alt.substring(0, 2) : "UN")}
          </span>
        )}
        {status && (
          <span
            className={cn(
              "absolute bottom-0 right-0 h-2.5 w-2.5 rounded-full border-2 border-background",
              status === "online" && "bg-emerald-500",
              status === "offline" && "bg-muted-foreground",
              status === "away" && "bg-amber-500"
            )}
          />
        )}
      </div>
    </AvatarContext.Provider>
  )
}
