import React, { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import api from '../api/axios'
import { Eye, EyeOff, TrendingUp, Sun, Moon } from 'lucide-react'
import { styles, tickerStyles } from '../styles/loginStyles'

export default function Login() {

  const { login, toggleTheme, theme } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState({ username: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [showPwd, setShowPwd] = useState(false)

  const [tickers, setTickers] = useState([])

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setError('')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')

    try {

      const res = await api.post('/auth/login', form)

      login(res.data)

      navigate(
        res.data.role === 'ROLE_ADMIN'
          ? '/admin'
          : '/dashboard'
      )

    } catch (err) {

      setError(
        err.response?.data?.message ||
        'Invalid username or password'
      )

    } finally {
      setLoading(false)
    }
  }

  /* FETCH TICKERS FROM BACKEND */

  useEffect(() => {

    const fetchTickers = async () => {

      try {

        const res = await api.get('/stocks/all')

        const data = res.data
        .slice(0, 4)
        .map(s => ({
          ticker: s.ticker,
          price: s.currentPrice,
          dir: s.priceChangeAmount > 0 ? "up" : "down"
        }))
        setTickers(data)

      } catch (err) {

        console.error("Failed to fetch tickers", err)

      }
    }

    fetchTickers()

  }, [])

  return (

    <div style={styles.root}>

      <div style={styles.gridBg} />

      <div style={{ ...styles.orb, ...styles.orbCyan }} />
      <div style={{ ...styles.orb, ...styles.orbAmber }} />

      <button
        onClick={toggleTheme}
        style={styles.themeBtn}
        title="Toggle theme"
      >
        {theme === 'dark'
          ? <Sun size={18} color="var(--accent-amber)" />
          : <Moon size={18} color="var(--accent-cyan)" />
        }
      </button>

      {/* LEFT PANEL */}

      <div style={styles.leftPanel}>

        <h1 style={styles.heroTitle}>
          TRADEMART
        </h1>

        <p style={styles.heroSubtitle}>
          A real-time stock exchange simulator designed for
          learning trading strategies and market behaviour.
        </p>

        <div style={styles.tickerStrip}>

          {tickers.map((t, i) => (

            <TickerItem
              key={i}
              ticker={t.ticker}
              price={t.price}
              dir={t.dir}
            />

          ))}

        </div>

      </div>

      {/* LOGIN CARD */}

      <div style={styles.card} className="animate-fadeInUp">

        <div style={styles.logoRow}>
          <div style={styles.logoIcon}>
            <TrendingUp size={22} color="var(--bg-primary)" />
          </div>
          <span style={styles.logoText}>TRADEMART</span>
        </div>

        <h1 style={styles.title}>Welcome back</h1>

        <p style={styles.subtitle}>
          Sign in to your trading account
        </p>

        {error && (

          <div style={styles.errorBanner} className="animate-fadeIn">
            <span style={{ fontSize: 14 }}>⚠</span>
            {error}
          </div>

        )}

        <form onSubmit={handleSubmit} style={styles.form}>

          <div style={styles.fieldGroup}>

            <label style={styles.label}>USERNAME</label>

            <input
              name="username"
              value={form.username}
              onChange={handleChange}
              placeholder="Enter your username"
              autoComplete="username"
              required
              style={styles.input}
              className="input"
            />

          </div>

          <div style={styles.fieldGroup}>

            <label style={styles.label}>PASSWORD</label>

            <div style={styles.pwdWrapper}>

              <input
                name="password"
                type={showPwd ? 'text' : 'password'}
                value={form.password}
                onChange={handleChange}
                placeholder="Enter your password"
                autoComplete="current-password"
                required
                style={{ ...styles.input, paddingRight: 44 }}
                className="input"
              />

              <button
                type="button"
                onClick={() => setShowPwd(p => !p)}
                style={styles.eyeBtn}
              >

                {showPwd
                  ? <EyeOff size={16} color="var(--text-muted)" />
                  : <Eye size={16} color="var(--text-muted)" />
                }

              </button>

            </div>

          </div>

          <button
            type="submit"
            disabled={loading}
            style={styles.submitBtn}
            className="btn btn-primary"
          >

            {loading
              ? <span style={styles.spinner} />
              : 'SIGN IN'
            }

          </button>

        </form>

        <div style={styles.dividerRow}>
          <div style={styles.dividerLine} />
          <span style={styles.dividerText}>or</span>
          <div style={styles.dividerLine} />
        </div>

        <p style={styles.registerText}>
          Don't have an account?{' '}
          <Link to="/register" style={styles.link}>
            Create one
          </Link>
        </p>

      </div>

    </div>
  )
}

/* TICKER COMPONENT */

function TickerItem({ ticker, price, dir }) {

    return (
      <div style={tickerStyles.item}>
  
        <span style={tickerStyles.ticker}>
          {ticker}
        </span>
  
        <span
          style={{
            ...tickerStyles.price,
            color: dir === 'up'
              ? 'var(--gain)'
              : 'var(--loss)'
          }}
        >
          ₹{Number(price).toLocaleString()}
        </span>
  
        <span
          style={{
            color: dir === 'up'
              ? 'var(--gain)'
              : 'var(--loss)',
            fontSize: 10
          }}
        >
          {dir === 'up' ? '▲' : '▼'}
        </span>
  
      </div>
    )
  }