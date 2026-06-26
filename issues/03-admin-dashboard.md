# PLUTO-3: Admin Dashboard for Problem CRUD Operations

## Parent
PRD — Project Pluto MVP

## What to build
Develop a secure portal for administrators to manage the system design problem catalog. Ensure that only users with the `admin` role can perform CRUD operations on `/api/problems` API endpoints, and design a clean UI at `/admin` to facilitate creation, update, and deletion of problems.

## Acceptance criteria
- [ ] Admin authorization middleware blocks non-admin users from accessing `/admin` pages and CRUD endpoints.
- [ ] Admin can create a new problem via UI form, which persists to the database via `POST /api/problems`.
- [ ] Admin can edit an existing problem via UI form, updating it via `PUT /api/problems/[id]`.
- [ ] Admin can delete a problem via UI, removing it from the database via `DELETE /api/problems/[id]`.
- [ ] API integration tests verify that regular users get `403 Forbidden` for all CRUD actions, while authenticated administrators succeed.

## Blocked by
- [PLUTO-1: Core DB Setup, Seeding & Problems Directory](file:///Users/adityavardhanagarwal/Projects/Pluto/issues/01-problems-catalog.md)
- [PLUTO-2: Local User Authentication & Secure Route Middleware](file:///Users/adityavardhanagarwal/Projects/Pluto/issues/02-local-auth.md)
