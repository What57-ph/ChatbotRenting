"use client"

import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { SettingsCard } from "./settings-card"

export function SecurityForm() {
  return (
    <div className="space-y-6">
      <SettingsCard 
        title="Update Password" 
        description="Ensure your account is using a long, random password to stay secure."
      >
        <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
          <div className="grid gap-2">
            <Label htmlFor="current_password">Current Password</Label>
            <Input id="current_password" type="password" />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="new_password">New Password</Label>
            <Input id="new_password" type="password" />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="confirm_password">Confirm Password</Label>
            <Input id="confirm_password" type="password" />
          </div>
          <div className="flex justify-end pt-4">
            <Button type="submit">Save Password</Button>
          </div>
        </form>
      </SettingsCard>

      <SettingsCard 
        title="Browser Sessions" 
        description="Manage and log out your active sessions on other browsers and devices."
      >
        <div className="space-y-4 text-sm text-muted-foreground">
          <p>
            If necessary, you may log out of all of your other browser sessions across all of your devices. Some of your recent sessions are listed below; however, this list may not be exhaustive. If you feel your account has been compromised, you should also update your password.
          </p>
          <div className="flex items-center gap-4">
            <div className="flex-1 space-y-1">
              <p className="font-medium text-foreground">Windows - Chrome</p>
              <p>192.168.1.1, This device</p>
            </div>
            <div className="text-emerald-500 font-medium">Active</div>
          </div>
          <div className="pt-4">
            <Button variant="destructive">Log Out Other Browser Sessions</Button>
          </div>
        </div>
      </SettingsCard>
    </div>
  )
}
