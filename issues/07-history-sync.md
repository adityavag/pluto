# PLUTO-7: Workspace Notes/Draft History & Completion Status Sync

## Parent
PRD — Project Pluto MVP

## What to build
Integrate submissions history. Provide a "History" interface in the workspace showing a list of previous drafts and notes for the current user and problem. Selecting a historical entry loads its state into the Excalidraw whiteboard. Also sync real completion statuses (Not Started, In Progress, Completed) to the Problems Directory list.

## Acceptance criteria
- [ ] `/api/submissions` supports fetching historical submissions for a specific user and problem.
- [ ] Workspace left panel features a "History" tab/view listing past saved drafts with timestamps.
- [ ] Clicking a history item fetches the draft state and replaces the current Excalidraw whiteboard elements and notes textarea content.
- [ ] Problems Directory query aggregates user submissions to display accurate status badges (`Not Started` / `In Progress` / `Completed`) for each problem.
- [ ] Unit and integration tests verify status calculations and correct canvas draft state restoration.

## Blocked by
- [PLUTO-6: Storage Abstraction & Submission Persistence (Auto-Save)](file:///Users/adityavardhanagarwal/Projects/Pluto/issues/06-submissions-storage.md)
