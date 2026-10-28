import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import App from './App'

describe('PlaceTrack App Component', () => {
  beforeEach(() => {
    // Mock global fetch to return empty arrays for all initial endpoints
    global.fetch = vi.fn((_url) => {
      return Promise.resolve({
        ok: true,
        status: 200,
        text: () => Promise.resolve('[]'),
        json: () => Promise.resolve([]),
      })
    })
  })

  it('renders PlaceTrack brand header and workspace navigation', async () => {
    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('PlaceTrack')).toBeDefined()
      expect(screen.getByText('Placement Portal')).toBeDefined()
      expect(screen.getByText('Registered Students')).toBeDefined()
      expect(screen.getByText('Partner Companies')).toBeDefined()
      expect(screen.getAllByText('Placement Drives').length).toBeGreaterThan(0)
      expect(screen.getAllByText('Applications').length).toBeGreaterThan(0)
    })
  })

  it('navigates to Students section and displays directory table', async () => {
    render(<App />)

    const studentsNavBtn = screen.getByRole('button', { name: /Students/i })
    fireEvent.click(studentsNavBtn)

    await waitFor(() => {
      expect(screen.getByText('Students Directory')).toBeDefined()
      expect(screen.getByPlaceholderText('Search Students…')).toBeDefined()
      expect(screen.getByRole('button', { name: /\+ Add Student/i })).toBeDefined()
    })
  })

  it('opens and cancels student create form', async () => {
    render(<App />)

    const studentsNavBtn = screen.getByRole('button', { name: /Students/i })
    fireEvent.click(studentsNavBtn)

    const addStudentBtn = await screen.findByRole('button', { name: /\+ Add Student/i })
    fireEvent.click(addStudentBtn)

    expect(screen.getByText('New Student')).toBeDefined()
    expect(screen.getByText('Student Name')).toBeDefined()
    expect(screen.getByText('Email Address')).toBeDefined()

    const cancelBtn = screen.getByRole('button', { name: /Cancel/i })
    fireEvent.click(cancelBtn)

    await waitFor(() => {
      expect(screen.queryByText('New Student')).toBeNull()
    })
  })

  it('navigates to Placement Drives and Companies sections', async () => {
    render(<App />)

    const companiesNavBtn = screen.getByRole('button', { name: /Companies/i })
    fireEvent.click(companiesNavBtn)

    await waitFor(() => {
      expect(screen.getByText('Companies Directory')).toBeDefined()
    })

    const drivesNavBtn = screen.getByRole('button', { name: /Placement Drives/i })
    fireEvent.click(drivesNavBtn)

    await waitFor(() => {
      expect(screen.getByText('Placement Drives Directory')).toBeDefined()
    })
  })
})
