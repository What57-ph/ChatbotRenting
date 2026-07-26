"use client"

import { motion } from "framer-motion"
import { Zap, Cpu, Shield } from "lucide-react"
import { Card } from "@/components/ui/card"

export function FeaturesSection() {
  return (
    <section id="features" className="pt-10 scroll-mt-32">
      <div className="text-center mb-16">
        <h2 className="text-3xl md:text-5xl font-bold tracking-tight mb-4 text-white">Unmatched Capability</h2>
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
  )
}
