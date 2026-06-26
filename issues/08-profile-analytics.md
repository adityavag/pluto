# PLUTO-8: User Profile Progress Dashboard & Gamification

## Parent
PRD — Project Pluto MVP

## What to build
Implement the profile page analytics. Design a user profile view `/profile` presenting a Git-style green activity contribution grid (visualizing submission frequencies over the past year), daily practice streak metrics, and a difficulty breakdown chart (Easy vs Medium vs Hard) using SVG.

## Acceptance criteria
- [ ] `/api/users/profile` aggregates user submission statistics (dates of activity, streak counts, difficulty breakdown).
- [ ] Profile page `/profile` renders a Git-style contribution grid displaying submission counts per day.
- [ ] Profile UI displays current and maximum practice streak counters.
- [ ] Profile UI renders a breakdown chart (e.g. pie/donut or stacked bar chart using SVG) representing solved problems by difficulty.
- [ ] Integration tests verify streak calculation algorithm against mock submission dates and ensure correct SVG layout rendering.

## Blocked by
- [PLUTO-6: Storage Abstraction & Submission Persistence (Auto-Save)](file:///Users/adityavardhanagarwal/Projects/Pluto/issues/06-submissions-storage.md)
