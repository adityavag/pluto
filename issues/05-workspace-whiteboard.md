# PLUTO-5: Split-Screen Workspace & Whiteboard UI

## Parent
PRD — Project Pluto MVP

## What to build
Create the core practice interface. This is a side-by-side workspace at `/problems/[slug]` where the left panel renders the problem description in markdown and secondary tabs (e.g. notes), and the right panel embeds the interactive Excalidraw whiteboard. Panels must be resizable via a draggable divider.

## Acceptance criteria
- [ ] Workspace page is routed dynamically at `/problems/[slug]` and loads the respective problem details.
- [ ] Left panel displays problem markdown and includes a tab for writing textual notes.
- [ ] Right panel loads `@excalidraw/excalidraw` dynamically on the client-side (`ssr: false`) to avoid hydration errors.
- [ ] Draggable divider component allows adjusting the split-screen panel widths dynamically.
- [ ] Workspace layout is responsive and fits comfortably on desktop viewports.
- [ ] Basic component/unit tests verify panel drag event handling and tab selection.

## Blocked by
- [PLUTO-1: Core DB Setup, Seeding & Problems Directory](file:///Users/adityavardhanagarwal/Projects/Pluto/issues/01-problems-catalog.md)
