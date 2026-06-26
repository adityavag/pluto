# PLUTO-1: Core DB Setup, Seeding & Problems Directory

## Parent
PRD — Project Pluto MVP

## What to build
Build the foundation for problem definition and display. Setup the MongoDB connection via Mongoose, define the Problem schema, create a seed script to load initial problems, and render a visually rich Problems Directory / Landing page using Next.js 14 App Router and TailwindCSS v3.

## Acceptance criteria
- [ ] MongoDB connection is established successfully during server startup.
- [ ] Mongoose `Problem` schema matches PRD specification (`title`, `slug`, `difficulty`, `tags`, `description`, `constraints`, `systemEstimates`).
- [ ] Seed script (`src/scripts/seed.js`) successfully populates database with at least 3 standard system design problems.
- [ ] Landing page/directory at root URL (`/` or `/problems`) displays a directory of seeded problems.
- [ ] Users can search problems by title/keywords in the UI.
- [ ] Users can filter the problem list by difficulty (Easy, Medium, Hard) or tags in the UI.
- [ ] Problems show a placeholder completion status badge.
- [ ] Integration tests verify the Mongoose model, database seeding, and API endpoint (`GET /api/problems`).

## Blocked by
None - can start immediately
