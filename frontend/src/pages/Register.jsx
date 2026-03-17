import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import api from '../api/axios'
import { Eye, EyeOff, TrendingUp, Sun, Moon, IndianRupee } from 'lucide-react'

import { styles } from '../styles/registerStyles'

export default function Register() {

  const { login, toggleTheme, theme } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState({
    username:'',
    email:'',
    password:'',
    initialBalance:''
  })

  const [error,setError] = useState('')
  const [loading,setLoading] = useState(false)
  const [showPwd,setShowPwd] = useState(false)
  const [step,setStep] = useState(1)

  const handleChange = (e)=>{
    setForm({...form,[e.target.name]:e.target.value})
    setError('')
  }

  const handleNextStep=(e)=>{
    e.preventDefault()

    if(!form.username || !form.email || !form.password){
      setError('Please fill in all fields')
      return
    }

    if(form.password.length < 6){
      setError('Password must be at least 6 characters')
      return
    }

    setError('')
    setStep(2)
  }

  const handleSubmit = async(e)=>{
    e.preventDefault()

    if(!form.initialBalance || parseFloat(form.initialBalance)<1000){
      setError('Minimum initial balance is ₹1,000')
      return
    }

    setLoading(true)
    setError('')

    try{

      const res = await api.post('/auth/register',{
        ...form,
        initialBalance:parseFloat(form.initialBalance)
      })

      login(res.data)
      navigate('/dashboard')

    }catch(err){

      setError(
        err.response?.data?.message ||
        'Registration failed. Please try again.'
      )

      setStep(1)

    }finally{
      setLoading(false)
    }
  }

  return (
    <div style={styles.root}>

      <div style={styles.gridBg}/>
      <div style={{...styles.orb,...styles.orbCyan}}/>
      <div style={{...styles.orb,...styles.orbAmber}}/>

      <button onClick={toggleTheme} style={styles.themeBtn}>
        {theme==='dark'
          ? <Sun size={18} color="var(--accent-amber)"/>
          : <Moon size={18} color="var(--accent-cyan)"/>
        }
      </button>

      <div style={styles.card} className="animate-fadeInUp">

        <div style={styles.logoRow}>
          <div style={styles.logoIcon}>
            <TrendingUp size={22} color="var(--bg-primary)"/>
          </div>
          <span style={styles.logoText}>TRADEMART</span>
        </div>

        <div style={styles.stepRow}>
          <StepDot num={1} active={step===1} done={step>1} label="Account"/>
          <div style={styles.stepLine}/>
          <StepDot num={2} active={step===2} done={false} label="Balance"/>
        </div>

        <h1 style={styles.title}>
          {step===1?'Create account':'Fund your account'}
        </h1>

        <p style={styles.subtitle}>
          {step===1
            ?'Start trading in minutes'
            :'Set your initial trading balance'
          }
        </p>

        {error && (
          <div style={styles.errorBanner}>
            ⚠ {error}
          </div>
        )}

        {step===1 && (
          <form onSubmit={handleNextStep} style={styles.form}>

            <div style={styles.fieldGroup}>
              <label style={styles.label}>USERNAME</label>
              <input
                name="username"
                value={form.username}
                onChange={handleChange}
                className="input"
                style={styles.inputStyle}
              />
            </div>

            <div style={styles.fieldGroup}>
              <label style={styles.label}>EMAIL</label>
              <input
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                className="input"
                style={styles.inputStyle}
              />
            </div>

            <div style={styles.fieldGroup}>
              <label style={styles.label}>PASSWORD</label>

              <div style={styles.pwdWrapper}>
                <input
                  name="password"
                  type={showPwd?'text':'password'}
                  value={form.password}
                  onChange={handleChange}
                  className="input"
                  style={{...styles.inputStyle,paddingRight:44}}
                />

                <button
                  type="button"
                  onClick={()=>setShowPwd(p=>!p)}
                  style={styles.eyeBtn}
                >
                  {showPwd
                    ? <EyeOff size={16}/>
                    : <Eye size={16}/>
                  }
                </button>

              </div>

            </div>

            <PasswordStrength password={form.password}/>

            <button className="btn btn-primary" style={styles.submitBtn}>
              CONTINUE
            </button>

          </form>
        )}

        {step===2 && (
          <form onSubmit={handleSubmit} style={styles.form}>

            <div style={styles.fieldGroup}>
              <label style={styles.label}>INITIAL BALANCE</label>

              <div style={styles.pwdWrapper}>

                <span style={styles.currencyIcon}>
                  <IndianRupee size={14}/>
                </span>

                <input
                  name="initialBalance"
                  type="number"
                  min="1000"
                  value={form.initialBalance}
                  onChange={handleChange}
                  className="input"
                  style={{...styles.inputStyle,paddingLeft:36}}
                />

              </div>

            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn btn-primary"
              style={styles.submitBtn}
            >
              {loading ? <span style={styles.spinner}/> : 'CREATE ACCOUNT'}
            </button>

          </form>
        )}

        <p style={styles.registerText}>
          Already have an account?{' '}
          <Link to="/login" style={styles.link}>Sign in</Link>
        </p>

      </div>

    </div>
  )
}

function StepDot({ num, active, done, label }) {
    return (
      <div style={{display:'flex',flexDirection:'column',alignItems:'center',gap:4}}>
        <div style={{
          width:28,
          height:28,
          borderRadius:'50%',
          display:'flex',
          alignItems:'center',
          justifyContent:'center',
          fontSize:12,
          fontWeight:700,
          background: done?'var(--gain)':active?'var(--accent-cyan)':'var(--bg-input)',
          color:(done||active)?'var(--bg-primary)':'var(--text-muted)'
        }}>
          {done?'✓':num}
        </div>
  
        <span style={{
          fontSize:10,
          color:active?'var(--accent-cyan)':'var(--text-muted)'
        }}>
          {label}
        </span>
      </div>
    )
}
  

  
function PasswordStrength({ password }) {

    if(!password) return null
  
    const strength =
      password.length>=12 && /[A-Z]/.test(password) && /[0-9]/.test(password) ? 3
      : password.length>=8 ? 2
      : password.length>=6 ? 1
      : 0
  
    const labels=['','Weak','Good','Strong']
    const colors=['','var(--loss)','var(--accent-amber)','var(--gain)']
  
    return(
      <div style={{display:'flex',flexDirection:'column',gap:4}}>
  
        <div style={{display:'flex',gap:4}}>
          {[1,2,3].map(i=>(
            <div key={i} style={{
              flex:1,
              height:3,
              background:i<=strength?colors[strength]:'var(--border-primary)'
            }}/>
          ))}
        </div>
  
        {strength>0 && (
          <span style={{
            fontSize:11,
            color:colors[strength],
            textAlign:'right'
          }}>
            {labels[strength]}
          </span>
        )}
  
      </div>
    )
  }