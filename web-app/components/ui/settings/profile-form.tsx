"use client"

import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { SettingsCard } from "./settings-card"

export function ProfileForm() {
  return (
    <SettingsCard 
      title="Profile Information" 
      description="Update your account's profile information and email address."
    >
      <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
        <div className="grid gap-2">
          <Label htmlFor="name">Full Name</Label>
          <Input id="name" defaultValue="John Doe" />
        </div>
        <div className="grid gap-2">
          <Label htmlFor="email">Email Address</Label>
          <Input id="email" type="email" defaultValue="johndoe@example.com" />
        </div>
        <div className="flex justify-end pt-4">
          <Button type="submit">Save Changes</Button>
        </div>
      </form>
    </SettingsCard>
  )
}
