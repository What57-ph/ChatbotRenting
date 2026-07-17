"use client"

import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { User, Shield, Bell } from "lucide-react"

import { ProfileForm } from "@/components/ui/settings/profile-form"
import { SecurityForm } from "@/components/ui/settings/security-form"
import { NotificationPreferences } from "@/components/ui/settings/notification-preferences"

export default function SettingsPage() {
  return (
    <div className="flex flex-col gap-6 p-6">
      <div className="flex flex-col gap-2">
        <h1 className="text-3xl font-bold tracking-tight">Settings</h1>
        <p className="text-muted-foreground">
          Manage your account settings and set e-mail preferences.
        </p>
      </div>

      <Tabs defaultValue="profile" className="flex flex-col gap-6 md:flex-row md:gap-10">
        <aside className="w-full md:w-64">
          <TabsList className="flex h-auto flex-row md:flex-col items-stretch justify-start bg-transparent p-0 w-full overflow-x-auto">
            <TabsTrigger
              value="profile"
              className="relative justify-start px-4 py-3 text-left data-[state=active]:bg-muted data-[state=active]:shadow-none data-[state=active]:font-medium transition-colors hover:bg-muted/50 w-full rounded-md"
            >
              <User className="mr-2 h-4 w-4" />
              Profile
            </TabsTrigger>
            <TabsTrigger
              value="security"
              className="relative justify-start px-4 py-3 text-left data-[state=active]:bg-muted data-[state=active]:shadow-none data-[state=active]:font-medium transition-colors hover:bg-muted/50 w-full rounded-md"
            >
              <Shield className="mr-2 h-4 w-4" />
              Security
            </TabsTrigger>
            <TabsTrigger
              value="notifications"
              className="relative justify-start px-4 py-3 text-left data-[state=active]:bg-muted data-[state=active]:shadow-none data-[state=active]:font-medium transition-colors hover:bg-muted/50 w-full rounded-md"
            >
              <Bell className="mr-2 h-4 w-4" />
              Notifications
            </TabsTrigger>
          </TabsList>
        </aside>

        <section className="flex-1 max-w-4xl">
          <TabsContent value="profile" className="m-0 mt-0">
            <ProfileForm />
          </TabsContent>
          <TabsContent value="security" className="m-0 mt-0">
            <SecurityForm />
          </TabsContent>
          <TabsContent value="notifications" className="m-0 mt-0">
            <NotificationPreferences />
          </TabsContent>
        </section>
      </Tabs>
    </div>
  )
}
