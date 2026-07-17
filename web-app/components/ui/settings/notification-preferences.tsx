"use client"

import { Switch } from "@/components/ui/switch"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { SettingsCard } from "./settings-card"

export function NotificationPreferences() {
  return (
    <SettingsCard 
      title="Notification Preferences" 
      description="Decide which communications you'd like to receive and how."
    >
      <form className="space-y-6" onSubmit={(e) => e.preventDefault()}>
        <div className="space-y-4">
          <div className="flex items-center justify-between space-x-2">
            <div className="space-y-1">
              <Label htmlFor="marketing_emails" className="text-base">Marketing Emails</Label>
              <p className="text-sm text-muted-foreground">Receive emails about new products, features, and more.</p>
            </div>
            <Switch id="marketing_emails" />
          </div>
          <div className="flex items-center justify-between space-x-2">
            <div className="space-y-1">
              <Label htmlFor="security_emails" className="text-base">Security Emails</Label>
              <p className="text-sm text-muted-foreground">Receive emails about your account security.</p>
            </div>
            <Switch id="security_emails" defaultChecked />
          </div>
          <div className="flex items-center justify-between space-x-2">
            <div className="space-y-1">
              <Label htmlFor="billing_emails" className="text-base">Billing Alerts</Label>
              <p className="text-sm text-muted-foreground">Get notified when you approach your token usage limit.</p>
            </div>
            <Switch id="billing_emails" defaultChecked />
          </div>
        </div>
        <div className="flex justify-end pt-4">
          <Button type="submit">Save Preferences</Button>
        </div>
      </form>
    </SettingsCard>
  )
}
