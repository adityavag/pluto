# PLUTO-2: Local User Authentication & Secure Route Middleware

## Parent
PRD — Project Pluto MVP

## What to build
Implement secure username/email/password sign-up, sign-in, and sign-out. Passwords must be hashed using bcrypt before saving. Sessions are maintained via HttpOnly JWT cookies. Secure routes (like profile and admin dashboards) are guarded by Next.js middleware checking the JWT cookie.

## Acceptance criteria
- [ ] Mongoose `User` schema contains `username`, `email`, encrypted `password`, and `role` (`'user'` or `'admin'`).
- [ ] `/api/auth/register` creates a user, hashes password with bcrypt, and sets HttpOnly JWT cookie.
- [ ] `/api/auth/login` validates credentials and sets HttpOnly JWT cookie.
- [ ] `/api/auth/logout` clears JWT session cookies.
- [ ] Next.js middleware protects secure routes (e.g. `/profile`, `/admin`), redirecting unauthenticated users to `/login`.
- [ ] Sign Up, Login, and Sign Out UI matches the responsive, glassmorphic styling system.
- [ ] API integration tests verify correct auth flows, error conditions (duplicate email, wrong password), and middleware protection.

## Blocked by
None - can start immediately
