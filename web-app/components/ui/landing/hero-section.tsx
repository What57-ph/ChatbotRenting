"use client"

import Link from "next/link"
import { motion } from "framer-motion"
import { Sparkles, ChevronRight } from "lucide-react"
import { Button } from "@/components/ui/button"

export function HeroSection() {
  return (
    <section className="flex flex-col items-center text-center">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full border border-white/10 bg-white/5 text-sm font-medium text-zinc-300 mb-8"
      >
        <Sparkles className="h-4 w-4 text-primary" />
        <span>Nexus AI Engine 2.0 is now live</span>
      </motion.div>
      
      <motion.h1
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.1 }}
        className="text-5xl md:text-7xl font-extrabold tracking-tighter text-transparent bg-clip-text bg-gradient-to-br from-white to-zinc-500 mb-6 max-w-4xl leading-tight"
      >
        Deploy Intelligent Agents in <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary to-indigo-400">Minutes</span>
      </motion.h1>
      
      <motion.p
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="text-lg md:text-xl text-zinc-400 max-w-2xl mb-10 leading-relaxed"
      >
        Lumina is the premium enterprise platform to rent, customize, and integrate advanced conversational AI models for your business workflow.
      </motion.p>
      
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.3 }}
        className="flex flex-col sm:flex-row items-center gap-4"
      >
        <Link href="/login">
          <Button size="lg" className="rounded-full shadow-xl shadow-primary/20 h-14 px-8 text-base">
            Start Renting Now <ChevronRight className="ml-2 h-4 w-4" />
          </Button>
        </Link>
        <Link href="#features">
          <Button variant="outline" size="lg" className="rounded-full h-14 px-8 text-base border-white/10 text-white hover:bg-white/5 bg-transparent">
            Explore Features
          </Button>
        </Link>
      </motion.div>
    </section>
  )
}
