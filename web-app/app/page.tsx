"use client"

import * as React from "react"
import Link from "next/link"
import { motion } from "framer-motion"
import { Bot, ChevronRight, Zap, Shield, Cpu, Sparkles } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-black text-zinc-50 font-sans selection:bg-primary/30 scroll-smooth">
      {/* Background Glows */}
      <div className="pointer-events-none absolute inset-0 flex justify-center overflow-hidden">
        <div className="absolute -top-[10%] w-[1000px] h-[500px] bg-primary/20 blur-[120px] rounded-[100%]" />
      </div>

      {/* Navigation */}
      <nav className="fixed top-0 inset-x-0 z-50 flex items-center justify-between px-6 lg:px-12 py-4 bg-black/50 backdrop-blur-md border-b border-white/5">
        <div className="flex items-center gap-2">
          <Bot className="h-8 w-8 text-primary" />
          <span className="text-xl font-bold tracking-tighter text-white">Lumina AI</span>
        </div>
        <div className="flex items-center gap-4">
          <Link href="/login" className="text-sm font-medium text-zinc-300 hover:text-white transition-colors">
            Sign In
          </Link>
          <Link href="/login">
            <Button className="rounded-full shadow-lg shadow-primary/25 hover:shadow-primary/40 transition-all font-semibold">
              Get Started
            </Button>
          </Link>
        </div>
      </nav>

      <main className="relative pt-32 pb-16 lg:pt-48 lg:pb-32 px-6 lg:px-12 max-w-7xl mx-auto flex flex-col gap-32">
        {/* Hero Section */}
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
              <Button variant="outline" size="lg" className="rounded-full h-14 px-8 text-base border-white/10 text-white hover:bg-white/5">
                Explore Features
              </Button>
            </Link>
          </motion.div>
        </section>

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

        {/* Features Bento Grid */}
        <section id="features" className="pt-10 scroll-mt-32">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-5xl font-bold tracking-tight mb-4">Unmatched Capability</h2>
            <p className="text-zinc-400 max-w-xl mx-auto text-lg">Built for performance, scalability, and security from day one.</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {[
              {
                icon: Zap,
                title: "Instant Deployment",
                desc: "Spin up a highly-tuned language model instance globally in less than 30 seconds."
              },
              {
                icon: Cpu,
                title: "Multi-Model Roster",
                desc: "Switch between GPT-4o, Claude 3.5, and Llama seamlessly without changing your integration code."
              },
              {
                icon: Shield,
                title: "Enterprise Grade Security",
                desc: "SOC2 compliant infrastructure with encrypted localized data processing for absolute privacy."
              }
            ].map((feature, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ duration: 0.5, delay: i * 0.1 }}
              >
                <Card className="bg-white/5 border-white/10 backdrop-blur-sm p-8 h-full flex flex-col hover:bg-white/[0.07] transition-all">
                  <div className="h-12 w-12 rounded-xl bg-primary/20 flex items-center justify-center mb-6 border border-primary/30">
                    <feature.icon className="h-6 w-6 text-primary" />
                  </div>
                  <h3 className="text-xl font-semibold text-white mb-2">{feature.title}</h3>
                  <p className="text-zinc-400 leading-relaxed">
                    {feature.desc}
                  </p>
                </Card>
              </motion.div>
            ))}
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className="border-t border-white/10 bg-black py-12 px-6 lg:px-12">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-6">
          <div className="flex items-center gap-2">
            <Bot className="h-6 w-6 text-primary" />
            <span className="font-semibold text-white tracking-tight">Lumina AI</span>
          </div>
          <p className="text-sm text-zinc-500">
            © {new Date().getFullYear()} Lumina Chatbot Renting Platform. All rights reserved.
          </p>
          <div className="flex items-center gap-4 text-sm text-zinc-400">
            <Link href="#" className="hover:text-white transition-colors">Privacy</Link>
            <Link href="#" className="hover:text-white transition-colors">Terms of Service</Link>
            <Link href="#" className="hover:text-white transition-colors">Contact</Link>
          </div>
        </div>
      </footer>
    </div>
  )
}
