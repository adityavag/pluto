# PLUTO-6: Storage Abstraction & Submission Persistence (Auto-Save)

## Parent
PRD — Project Pluto MVP

## What to build
Establish the persistence layer for diagrams and notes. Build a storage utility `src/lib/storage.js` that abstracts saving JSON drawing states and static PNG exports, falling back to local files in `public/uploads/` if S3/Supabase environment variables are missing. Hook this up to a client-side auto-save debouncer and a manual "Submit" button in the workspace.

## Acceptance criteria
- [ ] Mongoose `Submission` schema matches PRD specifications (`userId`, `problemId`, `diagramUrl`, `notes`, `status`, `submittedAt`).
- [ ] `src/lib/storage.js` handles saving files (JSON/PNG) to AWS S3/Supabase Storage or local fallback directory based on environment variables.
- [ ] API endpoint `POST /api/submissions` receives workspace state (notes, canvas elements), saves file assets to storage, and writes/updates metadata in MongoDB.
- [ ] UI debounces canvas and notes changes, auto-saving drafts (status: `'In Progress'`) periodically.
- [ ] Manual "Submit" button triggers a final save (status: `'Completed'`) and displays success feedback.
- [ ] Storage tests verify local file creation and S3 client mocking. Integration tests verify submission API route.

## Blocked by
- [PLUTO-2: Local User Authentication & Secure Route Middleware](file:///Users/adityavardhanagarwal/Projects/Pluto/issues/02-local-auth.md)
- [PLUTO-5: Split-Screen Workspace & Whiteboard UI](file:///Users/adityavardhanagarwal/Projects/Pluto/issues/05-workspace-whiteboard.md)
