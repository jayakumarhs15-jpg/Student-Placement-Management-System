import { useEffect, useState } from 'react'
import './App.css'

const sections = [
  { id: 'dashboard', label: 'Dashboard', icon: '▦' },
  { id: 'students', label: 'Students', icon: '♙' },
  { id: 'companies', label: 'Companies', icon: '▣' },
  { id: 'drives', label: 'Placement Drives', icon: '▤' },
  { id: 'applications', label: 'Applications', icon: '▧' },
]

const API = '/api'

function App() {
  const [active, setActive] = useState('dashboard')
  const [students, setStudents] = useState([])
  const [companies, setCompanies] = useState([])
  const [drives, setDrives] = useState([])
  const [applications, setApplications] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({})

  const endpoints = {
    students: '/students',
    companies: '/companies',
    drives: '/drives',
    applications: '/applications',
  }

  async function request(path, options = {}) {
    const response = await fetch(`${API}${path}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
    })

    if (!response.ok) {
      const message = await response.text()
      throw new Error(message || `Request failed (${response.status})`)
    }

    if (response.status === 204) return null

    const text = await response.text()
    return text ? JSON.parse(text) : null
  }

  async function loadData() {
    setLoading(true)
    setError('')

    try {
      const [studentData, companyData, driveData, applicationData] =
        await Promise.all([
          request('/students'),
          request('/companies'),
          request('/drives'),
          request('/applications'),
        ])

      setStudents(Array.isArray(studentData) ? studentData : [])
      setCompanies(Array.isArray(companyData) ? companyData : [])
      setDrives(Array.isArray(driveData) ? driveData : [])
      setApplications(Array.isArray(applicationData) ? applicationData : [])
    } catch (err) {
      setError(
        `Could not load backend data. Check that Spring Boot is running on port 8081. ${err.message}`,
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  function openForm(section) {
    setForm({})
    setNotice('')
    setShowForm(true)
  }

  async function submitForm(event) {
    event.preventDefault()
    setNotice('')
    setError('')

    try {
      const section = active
      const payload = { ...form }

      if (section === 'students') {
        payload.cgpa = Number(payload.cgpa)
        payload.graduationYear = String(payload.graduationYear)
      } else if (section === 'drives') {
        payload.minimumCgpa = Number(payload.minimumCgpa)
      }

      await request(endpoints[section], {
        method: 'POST',
        body: JSON.stringify(payload),
      })

      setShowForm(false)
      setNotice('Record created successfully.')
      await loadData()
    } catch (err) {
      setError(`Could not save record: ${err.message}`)
    }
  }

  const stats = [
    { label: 'Registered Students', value: students.length, icon: '♙', tone: 'blue' },
    { label: 'Partner Companies', value: companies.length, icon: '▣', tone: 'green' },
    { label: 'Placement Drives', value: drives.length, icon: '▤', tone: 'purple' },
    { label: 'Applications', value: applications.length, icon: '▧', tone: 'orange' },
  ]

  const titles = {
    dashboard: 'Dashboard',
    students: 'Students',
    companies: 'Companies',
    drives: 'Placement Drives',
    applications: 'Applications',
  }

  function renderTable() {
    if (active === 'students') {
      return (
        <Table
          columns={['ID', 'Name', 'Email', 'Department', 'CGPA', 'Graduation Year']}
          rows={students.map((s) => [
            s.id, s.name, s.email, s.department, s.cgpa, s.graduationYear,
          ])}
        />
      )
    }

    if (active === 'companies') {
      return (
        <Table
          columns={['ID', 'Company', 'Industry', 'Location', 'Contact Email']}
          rows={companies.map((c) => [
            c.id, c.name, c.industry, c.location, c.contactEmail,
          ])}
        />
      )
    }

    if (active === 'drives') {
      return (
        <Table
          columns={['ID', 'Job Title', 'Company', 'Location', 'Minimum CGPA', 'Deadline']}
          rows={drives.map((d) => [
            d.id, d.jobTitle, d.companyName, d.location, d.minimumCgpa, d.applicationDeadline,
          ])}
        />
      )
    }

    if (active === 'applications') {
      return (
        <Table
          columns={['ID', 'Student ID', 'Drive ID', 'Status', 'Applied At']}
          rows={applications.map((a) => [
            a.id, a.studentId, a.driveId, a.status, a.appliedAt,
          ])}
        />
      )
    }

    return null
  }

  function renderForm() {
    const fields = {
      students: [
        ['name', 'Student Name', 'text'],
        ['email', 'Email Address', 'email'],
        ['department', 'Department', 'text'],
        ['cgpa', 'CGPA (0–10)', 'number'],
        ['graduationYear', 'Graduation Year', 'text'],
      ],
      companies: [
        ['name', 'Company Name', 'text'],
        ['industry', 'Industry', 'text'],
        ['location', 'Location', 'text'],
        ['contactEmail', 'Contact Email', 'email'],
        ['description', 'Description', 'text'],
      ],
      drives: [
        ['jobTitle', 'Job Title', 'text'],
        ['companyName', 'Company Name', 'text'],
        ['location', 'Location', 'text'],
        ['minimumCgpa', 'Minimum CGPA', 'number'],
        ['applicationDeadline', 'Application Deadline', 'date'],
        ['description', 'Description', 'text'],
      ],
      applications: [
        ['studentId', 'Student ID', 'number'],
        ['driveId', 'Placement Drive ID', 'number'],
      ],
    }

    return (
      <form className="record-form" onSubmit={submitForm}>
        <div className="form-grid">
          {(fields[active] || []).map(([key, label, type]) => (
            <label key={key}>
              <span>{label}</span>
              <input
                required
                type={type}
                min={type === 'number' && key === 'cgpa' ? 0 : undefined}
                max={key === 'cgpa' || key === 'minimumCgpa' ? 10 : undefined}
                step={type === 'number' && (key === 'cgpa' || key === 'minimumCgpa') ? '0.1' : undefined}
                value={form[key] ?? ''}
                onChange={(event) =>
                  setForm({ ...form, [key]: event.target.value })
                }
              />
            </label>
          ))}
        </div>
        <div className="form-actions">
          <button type="button" className="button secondary" onClick={() => setShowForm(false)}>
            Cancel
          </button>
          <button type="submit" className="button primary">
            Save Record
          </button>
        </div>
      </form>
    )
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-icon">SP</div>
          <div>
            <h2>PlaceTrack</h2>
            <span>Placement Portal</span>
          </div>
        </div>

        <div className="nav-caption">WORKSPACE</div>
        <nav>
          {sections.map((section) => (
            <button
              key={section.id}
              className={`nav-item ${active === section.id ? 'active' : ''}`}
              onClick={() => {
                setActive(section.id)
                setShowForm(false)
                setNotice('')
                setError('')
              }}
            >
              <span className="nav-icon">{section.icon}</span>
              {section.label}
              {section.id === 'applications' && applications.length > 0 && (
                <span className="nav-count">{applications.length}</span>
              )}
            </button>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="status-dot" />
          <div>
            <strong>Backend API</strong>
            <span>localhost:8081</span>
          </div>
        </div>
      </aside>

      <main className="main-area">
        <header className="topbar">
          <div>
            <div className="breadcrumb">Workspace / <span>{titles[active]}</span></div>
            <h1>{titles[active]}</h1>
          </div>
          <div className="topbar-right">
            <div className="date-chip">{new Date().toLocaleDateString('en-IN', {
              day: '2-digit', month: 'short', year: 'numeric',
            })}</div>
            <div className="avatar">A</div>
          </div>
        </header>

        <div className="page-content">
          {error && (
            <div className="alert error-alert">
              <span>{error}</span>
              <button onClick={() => setError('')}>×</button>
            </div>
          )}

          {notice && (
            <div className="alert success-alert">
              {notice}
              <button onClick={() => setNotice('')}>×</button>
            </div>
          )}

          {active === 'dashboard' ? (
            <>
              <section className="welcome-banner">
                <div>
                  <span className="eyebrow">PLACEMENT OVERVIEW</span>
                  <h2>Build your future, one opportunity at a time.</h2>
                  <p>Manage students, connect with companies, and track placement applications in one place.</p>
                  <button className="button banner-button" onClick={() => {
                    setActive('drives')
                    setShowForm(false)
                  }}>
                    Explore Placement Drives <span>→</span>
                  </button>
                </div>
                <div className="banner-art" aria-hidden="true">
                  <div className="art-circle circle-one" />
                  <div className="art-circle circle-two" />
                  <div className="art-card">
                    <span>PLACEMENT</span>
                    <strong>Career<br />Launchpad</strong>
                    <div className="art-line" />
                    <div className="art-line short" />
                  </div>
                </div>
              </section>

              <div className="section-heading">
                <div>
                  <h2>Placement at a glance</h2>
                  <p>A live summary of your system records</p>
                </div>
                <button className="button secondary" onClick={loadData}>↻ Refresh</button>
              </div>

              <section className="stats-grid">
                {stats.map((stat) => (
                  <div className="stat-card" key={stat.label}>
                    <div className={`stat-icon ${stat.tone}`}>{stat.icon}</div>
                    <div className="stat-label">{stat.label}</div>
                    <div className="stat-bottom">
                      <strong>{loading ? '—' : stat.value}</strong>
                      <span className="stat-note">Total records</span>
                    </div>
                  </div>
                ))}
              </section>

              <section className="dashboard-lower">
                <div className="panel recent-panel">
                  <div className="panel-heading">
                    <div>
                      <h3>Recent Placement Drives</h3>
                      <p>Opportunities available in the system</p>
                    </div>
                    <button className="text-button" onClick={() => setActive('drives')}>View all →</button>
                  </div>
                  {loading ? <p className="empty-state">Loading placement drives…</p> :
                    drives.length === 0 ? <p className="empty-state">No placement drives found.</p> :
                    drives.slice(0, 4).map((drive) => (
                      <div className="drive-row" key={drive.id}>
                        <div className="company-avatar">{(drive.companyName || 'C').charAt(0)}</div>
                        <div className="drive-info">
                          <strong>{drive.jobTitle}</strong>
                          <span>{drive.companyName} · {drive.location}</span>
                        </div>
                        <div className="drive-cgpa">CGPA {drive.minimumCgpa}+</div>
                      </div>
                    ))}
                </div>

                <div className="panel activity-panel">
                  <div className="panel-heading">
                    <div>
                      <h3>Application Snapshot</h3>
                      <p>Current application status</p>
                    </div>
                  </div>
                  <div className="application-total">
                    <strong>{loading ? '—' : applications.length}</strong>
                    <span>Total applications</span>
                  </div>
                  {['APPLIED', 'SHORTLISTED', 'INTERVIEW', 'SELECTED', 'REJECTED'].map((status) => {
                    const count = applications.filter((a) => a.status === status).length
                    const percent = applications.length ? (count / applications.length) * 100 : 0
                    return (
                      <div className="status-item" key={status}>
                        <div className="status-label">
                          <span>{status.charAt(0) + status.slice(1).toLowerCase()}</span>
                          <strong>{count}</strong>
                        </div>
                        <div className="progress-track">
                          <div className={`progress-fill status-${status.toLowerCase()}`} style={{ width: `${percent}%` }} />
                        </div>
                      </div>
                    )
                  })}
                </div>
              </section>
            </>
          ) : (
            <section className="panel table-panel">
              <div className="panel-heading table-heading">
                <div>
                  <h2>{titles[active]} Directory</h2>
                  <p>View and manage records stored in your database.</p>
                </div>
                <div className="table-actions">
                  <button className="button secondary" onClick={loadData}>↻ Refresh</button>
                  {active !== 'applications' && (
                    <button className="button primary" onClick={() => openForm(active)}>
                      + Add {active === 'drives' ? 'Drive' : active.slice(0, -1).replace(/^./, (c) => c.toUpperCase())}
                    </button>
                  )}
                  {active === 'applications' && (
                    <button className="button primary" onClick={() => openForm(active)}>
                      + Apply to Drive
                    </button>
                  )}
                </div>
              </div>

              {showForm && renderForm()}

              {loading ? (
                <div className="empty-state">Loading records…</div>
              ) : (
                renderTable()
              )}
            </section>
          )}

          <footer className="page-footer">
            <span>© {new Date().getFullYear()} PlaceTrack · Student Placement Management System</span>
            <span>React + Spring Boot + MySQL</span>
          </footer>
        </div>
      </main>
    </div>
  )
}

function Table({ columns, rows }) {
  if (!rows.length) {
    return <div className="empty-state">No records found. Add a record to get started.</div>
  }

  return (
    <div className="table-scroll">
      <table>
        <thead>
          <tr>{columns.map((column) => <th key={column}>{column}</th>)}</tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={`${row[0]}-${index}`}>
              {row.map((cell, cellIndex) => (
                <td key={cellIndex}>
                  {cell === null || cell === undefined || cell === '' ? '—' : String(cell)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default App