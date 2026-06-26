# Product Requirements Document (PRD) — Project Pluto MVP

## Problem Statement

System design is a critical skill for software engineers, especially when preparing for interviews or designing large-scale software systems. However, learning system design is highly fragmented. Currently, learners have to juggle multiple disconnected resources: reading books/articles, watching videos, sketching diagrams on standalone digital whiteboards (like Excalidraw, Miro, or draw.io), documenting their ideas in separate markdown files, and manually keeping track of their practice progress. 

There is a lack of a unified, hands-on platform that combines system design challenge prompts, an interactive drawing workspace, progress tracking, and structured problem management in a single cohesive environment (modeled after the success of LeetCode for algorithmic coding).

## Solution

Project Pluto MVP is a unified system design learning platform that provides an integrated experience for engineers to practice and refine system design concepts. The key components of the solution include:

1. **Problems Directory**: A centralized catalog of system design problems categorized by difficulty (Easy, Medium, Hard) and tags (e.g., Caching, Sharding, Message Queue, Rate Limiter) with instant search and filter capabilities.
2. **Interactive Split-Screen Workspace**: A resizable side-by-side interface that displays the problem description/constraints on the left panel (with secondary tabs for notes and historical drafts) and an embedded Excalidraw whiteboard canvas on the right panel for immediate system diagram sketching.
3. **Progress Dashboard & User Profiles**: A profile view featuring Git-style activity grids (visualizing contribution frequency over time), daily practicing streaks, and statistics on problem completion categorized by difficulty.
4. **Administration Portal**: A secure dashboard allowing administrators to easily perform CRUD operations (Create, Read, Update, Delete) on problems.
5. **Robust Backend Integration**: Secure user authentication (supporting local email/password credentials and GitHub OAuth) and an abstract storage layer that handles diagram persistence (saving drawing states and static PNGs to Object Storage, with a seamless local directory fallback for offline local development).

## User Stories

1. As a visitor, I want to view a visually rich landing page highlighting Project Pluto's core features and popular problems, so that I can understand the value of the platform.
2. As a new user, I want to create an account using my username, email, and password, so that I can start tracking my learning progress.
3. As a registered user, I want to log in securely with my credentials, so that I can access my saved designs and history.
4. As a developer, I want to sign in using my GitHub account, so that I can quickly authenticate without creating new password credentials.
5. As an authenticated user, I want to log out, so that my session is cleared and my account remains secure on public computers.
6. As a user, I want to browse a comprehensive list of system design problems on a dedicated directory page, so that I can select a challenge to practice.
7. As a user, I want to search for problems by their titles or keywords, so that I can instantly locate specific challenges.
8. As a user, I want to filter the problem list by difficulty level (Easy, Medium, Hard) or tags (e.g., "WebSockets", "CDN"), so that I can practice topics I need to improve.
9. As a user, I want to see my completion status (e.g., Not Started, In Progress, Completed) next to each problem in the directory, so that I know what tasks to focus on next.
10. As a user, I want to open a problem and see a side-by-side split layout containing the markdown instructions on the left and a whiteboard on the right, so that I can design and read concurrently.
11. As a user, I want to resize the width of the split-screen panels using a draggable divider, so that I can optimize the layout for my screen dimensions.
12. As a user, I want to use standard shapes, arrows, colors, and text boxes in the embedded Excalidraw whiteboard, so that I can create clean and readable architecture diagrams.
13. As a user, I want to write text notes (e.g., explaining database design, data flow, scale estimates) in a notes tab, so that I can articulate the details of my system design.
14. As a user, I want my diagram drafts to automatically save periodically, so that I do not lose my work if my browser closes or reloads.
15. As a user, I want to submit my completed design, so that it gets cataloged as "Completed" and updates my learning activity stats.
16. As a user, I want to view my submission history for each problem and look at previous diagrams and notes, so that I can review my progress over time.
17. As a user, I want to view my profile page and see my daily practice streak, so that I stay motivated.
18. As a user, I want to see a GitHub-style green contribution grid of my practice submissions, so that I have a visual record of my consistency.
19. As a user, I want to see a breakdown chart showing the percentage of Easy, Medium, and Hard problems I have solved, so that I can monitor my learning progress.
20. As an administrator, I want to access a secure admin dashboard, so that I can manage the learning content catalog.
21. As an administrator, I want to create new problems with specific details (title, difficulty, tags, markdown description, constraints, and scale estimates), so that I can expand the platform's content.
22. As an administrator, I want to update existing problems to refine constraints or clarify descriptions, so that the learning material remains accurate and up-to-date.
23. As an administrator, I want to delete obsolete problems from the platform, so that the catalog remains clean and high-quality.

## Implementation Decisions

### Architectural Seams & Frameworks
- **Framework**: Next.js 14 using the App Router. The application will be constructed in JavaScript (non-TypeScript) using TailwindCSS v3 for responsive, glassmorphic styling.
- **Client-Side Rendering (CSR) for Whiteboard**: The `excalidraw-react` package requires browser-specific APIs (e.g., `window`, `navigator`) and must be dynamically loaded with server-side rendering disabled (`ssr: false`) to avoid hydration errors during Next.js page generation.
- **Database**: MongoDB managed via Mongoose. Mongoose schemas will enforce models for `User`, `Problem`, and `Submission`.

### Database Schema Shapes (Decisions from Prototype/Planning)
- **User Schema**: Contains standard authentication credentials (username, email, encrypted password), role hierarchy (role: `'user'` or `'admin'`), and optional `githubId` for OAuth linking.
- **Problem Schema**: Outlines system design tasks. Includes:
  - `title` and unique `slug` for clean URL routing.
  - `difficulty`: enum of `'Easy'`, `'Medium'`, `'Hard'`.
  - `tags`: array of strings for indexing.
  - `description`: long markdown string.
  - `constraints`: details of performance and resource specifications.
  - `systemEstimates`: traffic/storage estimates.
- **Submission Schema**: Binds users, problems, and diagram storage links. Includes:
  - `userId` (references User).
  - `problemId` (references Problem).
  - `diagramUrl`: URL pointing to the JSON/PNG file stored in object storage.
  - `notes`: textual markdown notes explaining system trade-offs.
  - `status`: `'In Progress'` | `'Completed'`.
  - `submittedAt`: timestamp of the last update.

### API Layer & Endpoints
- **Authentication**: JWT-based session cookies stored as HttpOnly, Secure, and SameSite=Lax.
- **Middleware**: A Next.js middleware boundary checks cookies on secure routes (e.g., `/admin`, `/profile`) and rejects unauthorized clients.
- **API Router Paths**:
  - `/api/auth/register`, `/api/auth/login`, `/api/auth/logout`: Manage JWT sessions.
  - `/api/auth/github`, `/api/auth/github/callback`: Handshake and sync profile data.
  - `/api/problems`: List problems (supporting filters and queries) and Create problems (Admin only).
  - `/api/problems/[id]`: Update and Delete problems (Admin only).
  - `/api/submissions`: Upload JSON/PNG file states and write metadata to MongoDB.

### Storage Abstraction Layer
- An abstraction layer (`src/lib/storage.js`) will determine file upload destinations:
  - If S3 environment variables are provided: Drawing states (JSON) and static exports (PNG) are pushed to S3/Supabase Storage.
  - Otherwise, fallback to saving files locally in `public/uploads` directory. This ensures zero-config local development setup for developers.

## Testing Decisions

### Seams and Test Environments
We will establish our testing at two primary seams to ensure high coverage and reliable execution:
1. **HTTP/API Integration Seam**: We will test our full backend API routes directly using endpoint requests. This acts as our primary test seam.
   - Tests will run in an isolated environment against a temporary MongoDB database (such as a local Docker instance or an in-memory database mock) to prevent cross-contamination.
   - External dependencies (such as S3 storage and GitHub OAuth requests) will be mocked out at this seam to keep tests deterministic and fast.
2. **Dynamic UI Rendering Seam**: We will verify page routing and component lifecycle (specifically the dynamic mounting of Excalidraw and resizable panel handlers) using automated web assertions or lightweight integration tests.

### Test Guidelines
- **External Behavior Focus**: Tests will focus on verifying external behavior (e.g., registering a user and logging in succeeds, retrieving the problem list returns correctly, submitting a drawing stores the files and updates stats). We will avoid testing internal helper implementations or private helper methods.
- **State Cleanliness**: Before each test block, databases will be cleared and pre-populated with deterministic seeds to ensure reproducible results.

## Out of Scope
- **Real-Time Collaboration**: Multi-user synchronous canvas drawing is deferred. Canvas sessions are strictly single-user.
- **Automated Grading/AI Scoring**: Evaluator AI modules (e.g., checking block connections, comparing architecture against standard topologies) are deferred for post-MVP.
- **Public Discussions / Comments**: Social comment threads under system design problems will not be included in the initial release.

## Further Notes
- A DB seeding script (`src/scripts/seed.js`) will be provided to load classic system design problems (e.g., "Design Twitter/X", "Design a Rate Limiter", "Design a Distributed Key-Value Store") to make testing immediately viable upon database installation.
