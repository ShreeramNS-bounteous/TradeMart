import React, { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user,  setUser]  = useState(null)
  const [token, setToken] = useState(null)
  const [theme, setTheme] = useState('dark')
  const [loading, setLoading] = useState(true)

  // ── Rehydrate on page refresh ──────────────────────────────
  useEffect(() => {
    const storedToken = localStorage.getItem('trademart_token')
    const storedUser  = localStorage.getItem('trademart_user')
    const storedTheme = localStorage.getItem('trademart_theme') || 'dark'

    if (storedToken && storedUser) {
      setToken(storedToken)
      setUser(JSON.parse(storedUser))
    }

    setTheme(storedTheme)
    document.documentElement.setAttribute('data-theme', storedTheme)
    setLoading(false)
  }, [])

  // ── Login — store token and user in state + localStorage ──
  const login = (authResponse) => {
    const { token, ...userData } = authResponse
    setToken(token)
    setUser(userData)
    localStorage.setItem('trademart_token', token)
    localStorage.setItem('trademart_user', JSON.stringify(userData))
  }

  // ── Logout — clear everything ──────────────────────────────
  const logout = () => {
    setToken(null)
    setUser(null)
    localStorage.removeItem('trademart_token')
    localStorage.removeItem('trademart_user')
  }

  // ── Theme toggle ───────────────────────────────────────────
  const toggleTheme = () => {
    const newTheme = theme === 'dark' ? 'light' : 'dark'
    setTheme(newTheme)
    document.documentElement.setAttribute('data-theme', newTheme)
    localStorage.setItem('trademart_theme', newTheme)
  }

  // ── Helpers ────────────────────────────────────────────────
  const isAdmin = () => user?.role === 'ROLE_ADMIN'
  const isAuthenticated = () => !!token

  return (
    <AuthContext.Provider value={{
      user, token, theme, loading,
      login, logout, toggleTheme,
      isAdmin, isAuthenticated
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}