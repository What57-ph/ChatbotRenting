import { CurrentPlan } from "@/components/ui/billing/current-plan"
import { PaymentMethods } from "@/components/ui/billing/payment-methods"
import { BillingHistory } from "@/components/ui/billing/billing-history"

export default function BillingPage() {
  return (
    <div className="max-w-5xl mx-auto space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
      
      {/* Page Header */}
      <div>
        <h1 className="text-3xl font-bold tracking-tight text-white mb-1">Billing & Subscription</h1>
        <p className="text-zinc-400">Manage your subscription plan, payment methods, and billing history.</p>
      </div>

      {/* Top Section: Plan Overview */}
      <CurrentPlan />

      {/* Bottom Section: Payment Methos & History */}
      <div className="grid lg:grid-cols-2 gap-6 items-stretch">
        <PaymentMethods />
        <BillingHistory />
      </div>

    </div>
  )
}
