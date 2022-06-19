import { cleanup, render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter as Router } from 'react-router-dom';
import { useAuth } from '../auth/Auth';
import { AuthNav } from './NavHeader';

jest.mock("../auth/Auth")
const useMockAuth = jest.mocked(useAuth);

const mockAuthContext = {
  user: {
    username: "Anonymous",
    token: null
  },
  signIn: Promise.resolve,
  signOut: Promise.resolve,
  isSignedIn: () => true
};

afterEach(cleanup)

it('renders auth navigation with login button disabled when signed in', async () => {
  useMockAuth.mockReturnValue(mockAuthContext)

  render(
    <Router>
      <AuthNav onSignOut={function (): void {
        throw new Error('Function not implemented.');
      }} />
    </Router>
  )

  await waitFor(() => {
    expect(screen.getByTestId("auth-nav-menu")).toBeInTheDocument();
    expect(screen.getByTestId("auth-nav-menu-login")).toHaveAttribute("aria-disabled", "true");
    expect(screen.getByTestId("auth-nav-menu-logout")).toHaveAttribute("aria-disabled", "false");
  })
})

it('renders auth navigation with logout button disabled when signed out', async () => {
  useMockAuth.mockReturnValue(Object.assign(mockAuthContext, { isSignedIn: () => false }))
  expect(useMockAuth)

  render(
    <Router>
      <AuthNav onSignOut={function (): void {
        throw new Error('Function not implemented.');
      }} />
    </Router>
  )

  await waitFor(() => {
    expect(screen.getByTestId("auth-nav-menu")).toBeInTheDocument();
    expect(screen.getByTestId("auth-nav-menu-login")).toHaveAttribute("aria-disabled", "false");
    expect(screen.getByTestId("auth-nav-menu-logout")).toHaveAttribute("aria-disabled", "true");
  })
})
