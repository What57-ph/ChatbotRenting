"use client"
import React, { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { useDispatch } from "react-redux"
import { login } from "@/redux/slices/authSlice"
import { BotMessageSquare, Sparkles } from "lucide-react"

export default function LoginPage() {
  const router = useRouter()
  const dispatch = useDispatch()
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [loading, setLoading] = useState(false)

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    
    // Simulate API call
    setTimeout(() => {
      dispatch(login({ id: "usr_123", name: "Lumina User", role: "admin" }))
      setLoading(false)
      // Chuyển hướng sau đăng nhập
      router.push("/dashboard")
    }, 1000)
  }

  return (
    <div className="min-h-screen grid grid-cols-1 md:grid-cols-2">
      {/* Cột trái: Hình ảnh / Branding (Glassmorphism backdrop) */}
      <div className="hidden md:flex flex-col justify-center items-center bg-zinc-900 bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-indigo-900 via-zinc-900 to-black text-white p-12 relative overflow-hidden">
        {/* Glow effect */}
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-primary/20 blur-[100px] rounded-full" />
        
        <div className="z-10 text-center max-w-md">
          <div className="flex items-center justify-center space-x-3 mb-6">
            <BotMessageSquare className="w-12 h-12 text-primary" />
            <h1 className="text-4xl font-bold tracking-tight text-white">Lumina AI</h1>
          </div>
          <p className="text-lg text-zinc-400 mb-8">
            Empower your business with next-generation conversational AI. Build, rent, and scale intelligent chatbots seamlessly.
          </p>
          <div className="inline-flex items-center space-x-2 bg-white/5 border border-white/10 rounded-full px-4 py-2 text-sm text-zinc-300">
            <Sparkles className="w-4 h-4 text-primary" />
            <span>Premium SaaS Platform</span>
          </div>
        </div>
      </div>

      {/* Cột phải: Form Đăng nhập */}
      <div className="flex items-center justify-center p-8 bg-zinc-50 dark:bg-zinc-950">
        <Card className="w-full max-w-md border-0 shadow-xl bg-white dark:bg-zinc-900/50 backdrop-blur-sm">
          <CardHeader className="space-y-2 text-center pb-8">
            <div className="md:hidden flex items-center justify-center space-x-2 mb-4">
              <BotMessageSquare className="w-8 h-8 text-primary" />
              <span className="text-xl font-bold">Lumina AI</span>
            </div>
            <CardTitle className="text-3xl font-semibold tracking-tight">Welcome back</CardTitle>
            <CardDescription className="text-zinc-500">
              Enter your credentials to access your account
            </CardDescription>
          </CardHeader>
          
          <form onSubmit={handleLogin}>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <label className="text-sm font-medium leading-none text-zinc-700 dark:text-zinc-300" htmlFor="email">
                  Email
                </label>
                <Input 
                  id="email" 
                  type="email" 
                  placeholder="name@example.com" 
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  className="h-11 dark:border-zinc-800 dark:bg-zinc-900"
                />
              </div>
              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <label className="text-sm font-medium leading-none text-zinc-700 dark:text-zinc-300" htmlFor="password">
                    Password
                  </label>
                  <a href="#" className="text-sm font-medium text-primary hover:underline">
                    Forgot password?
                  </a>
                </div>
                <Input 
                  id="password" 
                  type="password" 
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  className="h-11 dark:border-zinc-800 dark:bg-zinc-900"
                />
              </div>
            </CardContent>
            <CardFooter className="flex flex-col gap-4 mt-4">
              <Button type="submit" className="w-full h-11 text-base font-medium shadow-lg hover:shadow-primary/25 transition-all" disabled={loading}>
                {loading ? "Signing in..." : "Sign in"}
              </Button>
              <div className="text-center text-sm text-zinc-500">
                Don't have an account?{" "}
                <a href="#" className="font-semibold text-primary hover:underline">
                  Sign up
                </a>
              </div>
            </CardFooter>
          </form>
        </Card>
      </div>
    </div>
  )
}
