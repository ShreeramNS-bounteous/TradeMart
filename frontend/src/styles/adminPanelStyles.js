export const s = {
    root: {
      minHeight: '100vh',
      background: 'var(--bg-primary)'
    },
  
    page: {
      marginLeft: 240,
      padding: '36px 32px',
      maxWidth: 1400
    },
  
    header: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'flex-start',
      marginBottom: 20,
      flexWrap: 'wrap',
      gap: 18
    },
  
    title: {
      fontSize: 30,
      letterSpacing: '-0.03em',
      marginBottom: 4
    },
  
    subtitle: {
      fontSize: 13,
      fontFamily: 'var(--font-mono)',
      color: 'var(--text-secondary)'
    },
  
    headerActions: {
      display: 'flex',
      alignItems: 'center',
      gap: 10,
      flexWrap: 'wrap'
    },
  
    actionMsg: {
      background: 'var(--gain-dim)',
      border: '1px solid var(--gain)',
      borderRadius: 'var(--radius-md)',
      padding: '8px 14px',
      color: 'var(--gain)',
      fontSize: 12,
      fontFamily: 'var(--font-mono)'
    },
  
    marketBanner: {
      display: 'flex',
      alignItems: 'center',
      gap: 10,
      padding: '10px 18px',
      borderRadius: 'var(--radius-md)',
      border: '1px solid var(--border-primary)',
      background: 'var(--bg-card)',
      marginBottom: 26,
      width: 'fit-content',
      fontFamily: 'var(--font-mono)',
      fontSize: 12
    },
  
    tabRow: {
      display: 'flex',
      gap: 6,
      marginBottom: 24,
      borderBottom: '1px solid var(--border-primary)'
    },
  
    tab: {
      display: 'flex',
      alignItems: 'center',
      gap: 6,
      padding: '12px 18px',
      background: 'none',
      border: 'none',
      borderBottom: '2px solid transparent',
      cursor: 'pointer',
      fontFamily: 'var(--font-ui)',
      fontSize: 13,
      fontWeight: 700,
      color: 'var(--text-muted)',
      letterSpacing: '0.05em',
      textTransform: 'uppercase',
      transition: 'var(--transition)',
      marginBottom: '-1px'
    },
  
    tabActive: {
      color: 'var(--accent-cyan)',
      borderBottomColor: 'var(--accent-cyan)'
    },
  
    tabBadge: {
      color: 'var(--bg-primary)',
      borderRadius: '50%',
      width: 18,
      height: 18,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      fontSize: 10,
      fontWeight: 700
    },
  
    summaryGrid: {
      display: 'flex',
      gap: 18,
      flexWrap: 'wrap'
    },
  
    sectionTitle: {
      fontFamily: 'var(--font-display)',
      fontSize: 18,
      fontWeight: 700,
      letterSpacing: '-0.01em',
      marginBottom: 18,
      color: 'var(--text-primary)'
    },
  
    roleBadge: {
      display: 'inline-flex',
      padding: '3px 10px',
      borderRadius: 20,
      fontSize: 10,
      fontFamily: 'var(--font-mono)',
      fontWeight: 700,
      letterSpacing: '0.08em',
      background: 'var(--accent-cyan-dim)',
      color: 'var(--accent-cyan)'
    },
  
    emptyMargin: {
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '64px',
      background: 'var(--bg-card)',
      border: '1px solid var(--border-primary)',
      borderRadius: 'var(--radius-lg)',
      textAlign: 'center'
    },
  
    /* STOCK FORM */
  
    stockFormCard: {
      background: 'var(--bg-card)',
      border: '1px solid var(--border-primary)',
      borderRadius: 'var(--radius-lg)',
      padding: '26px',
      boxShadow: 'var(--shadow-card)'
    },
  
    stockFormGrid: {
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))',
      gap: 18
    },
  
    stockFormField: {
      display: 'flex',
      flexDirection: 'column',
      gap: 6
    },
  
    stockFormLabel: {
      fontSize: 11,
      fontFamily: 'var(--font-mono)',
      color: 'var(--text-muted)',
      letterSpacing: '0.08em',
      fontWeight: 700
    },
  
    stockFormError: {
      marginTop: 14,
      padding: '10px 14px',
      background: 'var(--loss-dim)',
      border: '1px solid var(--loss)',
      borderRadius: 'var(--radius-md)',
      color: 'var(--loss)',
      fontSize: 12,
      fontFamily: 'var(--font-mono)'
    }
  }
  
  export const st = {
  
    card: {
      padding: '24px',
      flex: '1 1 160px',
      transition: 'var(--transition)',
      cursor: 'default'
    },
  
    icon: {
      width: 46,
      height: 46,
      borderRadius: 'var(--radius-md)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      marginBottom: 16
    },
  
    value: {
      fontSize: 34,
      fontFamily: 'var(--font-mono)',
      fontWeight: 700,
      marginBottom: 6,
      letterSpacing: '-0.03em'
    },
  
    label: {
      fontSize: 11,
      color: 'var(--text-muted)',
      fontFamily: 'var(--font-mono)',
      letterSpacing: '0.08em',
      textTransform: 'uppercase'
    }
  }