"use client"

import { motion } from "framer-motion"
import { SiteHeader } from "@/components/ui/landing/site-header"
import { HeroSection } from "@/components/ui/landing/hero-section"
import { FeaturesSection } from "@/components/ui/landing/features-section"
import { SiteFooter } from "@/components/ui/landing/site-footer"

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-black text-zinc-50 font-sans selection:bg-primary/30 scroll-smooth">
      {/* Background Glows */}
      <div className="pointer-events-none absolute inset-0 flex justify-center overflow-hidden">
        <div className="absolute -top-[10%] w-[1000px] h-[500px] bg-primary/20 blur-[120px] rounded-[100%]" />
      </div>

      <SiteHeader />

      <main className="relative pt-32 pb-16 lg:pt-48 lg:pb-32 px-6 lg:px-12 max-w-7xl mx-auto flex flex-col gap-32">
        <HeroSection />

        {/* Dynamic Image / Dashboard Mockup */}
        <motion.section
          initial={{ opacity: 0, y: 40 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.7 }}
          className="relative rounded-2xl border border-white/10 bg-white/5 p-2 overflow-hidden shadow-2xl backdrop-blur-sm"
        >
          <div className="absolute inset-0 bg-gradient-to-t from-black via-transparent to-transparent z-10" />
          <div className="rounded-xl overflow-hidden border border-white/10 bg-zinc-950 aspect-video flex items-center justify-center relative">
             {/* Mockup UI representation */}
             <div className="absolute inset-x-0 top-0 h-10 border-b border-white/10 bg-zinc-900 flex items-center px-4 gap-2">
                <div className="w-3 h-3 rounded-full bg-rose-500/80" />
                <div className="w-3 h-3 rounded-full bg-amber-500/80" />
                <div className="w-3 h-3 rounded-full bg-emerald-500/80" />
             </div>
             <div className="w-full h-full pt-10 px-6 pb-6 flex gap-6 opacity-60">
                <div className="w-1/4 rounded-lg bg-white/5 border border-white/5" />
                <div className="flex-1 flex flex-col gap-4">
                  <div className="h-1/3 rounded-lg bg-white/5 border border-white/5" />
                  <div className="flex-1 flex gap-4">
                     <div className="flex-1 rounded-lg bg-white/5 border border-white/5" />
                     <div className="flex-1 rounded-lg bg-white/5 border border-white/5" />
                  </div>
                </div>
             </div>
          </div>
        </motion.section>

        <FeaturesSection />
      </main>

      <SiteFooter />
    </div>
  )
}
