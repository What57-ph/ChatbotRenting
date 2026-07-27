import { Metadata } from "next"
import { AuthBranding } from "@/components/ui/auth/auth-branding"
import { RegisterForm } from "@/components/ui/auth/register-form"

export const metadata: Metadata = {
  title: "Create an account | Lumina AI",
  description: "Create your Lumina AI account to build and rent chatbots.",
}

export default function RegisterPage() {
  return (
    <div className="grid min-h-screen grid-cols-1 md:grid-cols-2">
      <AuthBranding />
      <RegisterForm />
    </div>
  )
}
