# PLUTO-4: GitHub OAuth Authentication

## Parent
PRD — Project Pluto MVP

## What to build
Implement third-party authentication using GitHub OAuth. Users should be able to click a "Sign in with GitHub" button, log in via GitHub, and be redirected back to Pluto where their session is initialized. Link GitHub identities to Mongoose users using `githubId`.

## Acceptance criteria
- [ ] `User` schema supports optional `githubId`.
- [ ] `/api/auth/github` redirects user to GitHub OAuth authorization endpoint.
- [ ] `/api/auth/github/callback` exchanges oauth code for github user details, finds or creates the corresponding user record in MongoDB, and signs the user in via JWT cookie.
- [ ] Sign in page contains a styled GitHub sign-in button.
- [ ] Integration tests use mocks to simulate GitHub auth callback and verify correct user creation and session creation.

## Blocked by
- [PLUTO-2: Local User Authentication & Secure Route Middleware](file:///Users/adityavardhanagarwal/Projects/Pluto/issues/02-local-auth.md)
