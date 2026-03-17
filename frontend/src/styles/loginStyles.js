export const tickerStyles = {
    item: {
      display: 'flex',
      alignItems: 'center',
      gap: 8,
    },
    ticker: {
      fontSize: 11,
      fontFamily: 'var(--font-mono)',
      color: 'var(--text-muted)',
      letterSpacing: '0.08em',
    },
    price: {
      fontSize: 13,
      fontFamily: 'var(--font-mono)',
      fontWeight: 700,
    }
  }
  
  export const styles = {
  
    /* PAGE ROOT */
    root: {
      minHeight: '100vh',
      display: 'grid',
      gridTemplateColumns: '1fr 420px',
      alignItems: 'center',
      gap: '60px',
      background: 'var(--bg-primary)',
      position: 'relative',
      overflow: 'hidden',
      padding: '40px',
    },
  
    /* LEFT COLUMN */
    leftPanel: {
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      gap: 24,
      maxWidth: 520,
    },
  
    heroTitle: {
      fontFamily: 'var(--font-display)',
      fontSize: 42,
      fontWeight: 800,
      color: 'var(--text-primary)',
      letterSpacing: '-0.03em',
      lineHeight: 1.1,
    },
  
    heroSubtitle: {
      fontFamily: 'var(--font-mono)',
      fontSize: 14,
      color: 'var(--text-secondary)',
      maxWidth: 420,
    },
  
    gridBg: {
      position: 'absolute',
      inset: 0,
      backgroundImage: `
        linear-gradient(var(--border-primary) 1px, transparent 1px),
        linear-gradient(90deg, var(--border-primary) 1px, transparent 1px)
      `,
      backgroundSize: '48px 48px',
      opacity: 0.25,
      pointerEvents: 'none',
    },
  
    orb: {
      position: 'absolute',
      borderRadius: '50%',
      filter: 'blur(100px)',
      pointerEvents: 'none',
      opacity: 0.2,
    },
  
    orbCyan: {
      width: 420,
      height: 420,
      background: 'radial-gradient(circle, var(--accent-cyan), transparent 70%)',
      top: '-10%',
      right: '-10%',
    },
  
    orbAmber: {
      width: 320,
      height: 320,
      background: 'radial-gradient(circle, var(--accent-amber), transparent 70%)',
      bottom: '-10%',
      left: '-5%',
    },
  
    themeBtn: {
      position: 'absolute',
      top: 24,
      right: 24,
      background: 'var(--bg-card)',
      border: '1px solid var(--border-primary)',
      borderRadius: '50%',
      width: 42,
      height: 42,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      cursor: 'pointer',
      transition: 'var(--transition)',
      zIndex: 10,
      boxShadow: 'var(--shadow-card)'
    },
  
    /* LOGIN CARD (RIGHT COLUMN) */
    card: {
      position: 'relative',
      zIndex: 1,
      background: 'var(--bg-card)',
      border: '1px solid var(--border-primary)',
      borderRadius: 'var(--radius-xl)',
      padding: '36px',
      width: '100%',
      maxWidth: '420px',
      boxShadow: 'var(--shadow-card), var(--shadow-glow-cyan)',
    },
  
    logoRow: {
      display: 'flex',
      alignItems: 'center',
      gap: 12,
      marginBottom: 28,
    },
  
    logoIcon: {
      width: 42,
      height: 42,
      background: 'linear-gradient(135deg, var(--accent-cyan), var(--accent-amber))',
      borderRadius: 'var(--radius-md)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      boxShadow: 'var(--shadow-glow-cyan)',
    },
  
    logoText: {
      fontFamily: 'var(--font-display)',
      fontSize: 22,
      fontWeight: 800,
      color: 'var(--text-primary)',
      letterSpacing: '-0.02em',
    },
  
    title: {
      fontSize: 26,
      marginBottom: 6,
      letterSpacing: '-0.02em',
    },
  
    subtitle: {
      fontSize: 13,
      color: 'var(--text-secondary)',
      marginBottom: 24,
      fontFamily: 'var(--font-mono)',
    },
  
    errorBanner: {
      display: 'flex',
      alignItems: 'center',
      gap: 8,
      background: 'var(--loss-dim)',
      border: '1px solid var(--loss)',
      borderRadius: 'var(--radius-md)',
      padding: '10px 14px',
      color: 'var(--loss)',
      fontSize: 13,
      fontFamily: 'var(--font-mono)',
      marginBottom: 18,
    },
  
    form: {
      display: 'flex',
      flexDirection: 'column',
      gap: 18,
    },
  
    fieldGroup: {
      display: 'flex',
      flexDirection: 'column',
      gap: 6,
    },
  
    label: {
      fontSize: 11,
      fontFamily: 'var(--font-mono)',
      color: 'var(--text-muted)',
      letterSpacing: '0.1em',
      fontWeight: 700,
    },
  
    input: {
      width: '100%',
    },
  
    pwdWrapper: {
      position: 'relative',
    },
  
    eyeBtn: {
      position: 'absolute',
      right: 12,
      top: '50%',
      transform: 'translateY(-50%)',
      background: 'none',
      border: 'none',
      cursor: 'pointer',
      padding: 4,
      display: 'flex',
      alignItems: 'center',
    },
  
    submitBtn: {
      width: '100%',
      justifyContent: 'center',
      padding: '14px',
      fontSize: 14,
      marginTop: 8,
    },
  
    dividerRow: {
      display: 'flex',
      alignItems: 'center',
      gap: 12,
      margin: '24px 0 16px',
    },
  
    dividerLine: {
      flex: 1,
      height: 1,
      background: 'var(--border-primary)',
    },
  
    dividerText: {
      fontSize: 12,
      color: 'var(--text-muted)',
      fontFamily: 'var(--font-mono)',
    },
  
    registerText: {
      textAlign: 'center',
      fontSize: 13,
      color: 'var(--text-secondary)',
      fontFamily: 'var(--font-mono)',
    },
  
    link: {
      color: 'var(--accent-cyan)',
      textDecoration: 'none',
      fontWeight: 700,
    },
  
    tickerStrip: {
      display: 'flex',
      gap: 20,
      marginTop: 20,
      paddingTop: 20,
      borderTop: '1px solid var(--border-primary)',
    },
  
    spinner: {
      width: 16,
      height: 16,
      border: '2px solid rgba(255,255,255,0.2)',
      borderTop: '2px solid var(--accent-cyan)',
      borderRadius: '50%',
      display: 'inline-block',
      animation: 'spin 0.8s linear infinite',
    },
  }