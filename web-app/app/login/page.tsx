import { Metadata } from "next"
import { AuthBranding } from "@/components/ui/auth/auth-branding"
import { LoginForm } from "@/components/ui/auth/login-form"

export const metadata: Metadata = {
  title: "Sign In | Lumina AI",
  description: "Sign in to your Lumina AI account to manage your chatbots.",
}

export default function LoginPage() {
  return (
    <div className="grid min-h-screen grid-cols-1 md:grid-cols-2">
      <AuthBranding />
      <LoginForm />
    </div>
  )
}
