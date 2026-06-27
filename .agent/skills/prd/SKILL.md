---
name: prd
description: >
  Converts a seed document, feature list, conversation, or rough brief into a
  structured Product Requirements Document (PRD). Trigger this whenever the
  user says "write the PRD", "generate a PRD", "turn this into a PRD",
  "create product requirements", or "we're ready to write the PRD". Also
  trigger when the user has finished an architectural debate or planning session
  and wants to formalise the decisions into a document. The output is a
  complete, ticket-ready PRD that can be directly fed into the `to-issues`
  skill.
---

# To-PRD — Product Requirements Document Generator

You are a product engineer who writes precise, developer-friendly PRDs.
Your PRDs are grounded in what was actually decided — not aspirational bloat.
Everything in the PRD must be traceable to the project context or stated goals.

---

## Inputs

Before writing, extract the following from the conversation / seed doc:

| Field | Where to find it |
|---|---|
| Product name | Seed doc or user message |
| Problem statement | Seed doc §2 or user description |
| Target users | Seed doc §3 |
| MVP scope | Seed doc §4 or debate conclusions |
| Tech stack | Seed doc §6 or architectural decisions |
| Constraints | Seed doc §5 (budget, team, infra) |
| Non-goals | Seed doc §7 |
| Success metrics | Seed doc §8 |

If any field is missing, make a reasonable assumption and flag it with `⚠️ Assumed:`.

---

## PRD Structure

Output the PRD in this exact structure:

```markdown
# PRD: [Product Name]

**Status:** Draft  
**Version:** 1.0  
**Authors:** [Team]  
**Last Updated:** [Date]

---

## 1. Overview
One paragraph. What is this, who is it for, why does it matter.

---

## 2. Problem Statement
What pain are we solving? What does the world look like without this product?

---

## 3. Goals & Non-Goals

### Goals (MVP)
- ...

### Non-Goals
- ...

---

## 4. Target Users

### Primary
[Description + key traits]

### Secondary (if any)
[Description]

---

## 5. User Stories

Format each as:
> As a [user type], I want to [action] so that [outcome].

Group by feature area. Each story maps to one or more tickets later.

### [Feature Area 1]
- US-001: As a ...
- US-002: As a ...

### [Feature Area 2]
- US-003: As a ...

---

## 6. Functional Requirements

For each user story, list the concrete system behaviours required.

### FR-001: [Requirement name]
- **Story:** US-001
- **Description:** ...
- **Acceptance criteria:**
  - [ ] ...
  - [ ] ...

---

## 7. Non-Functional Requirements

| Requirement | Target | Notes |
|---|---|---|
| Performance | e.g. p95 page load < 2s | |
| Availability | e.g. 99% uptime | Free tier constraint |
| Security | e.g. Auth required for all writes | |
| Cost | $0/month (AWS free tier) | Hard constraint |

---

## 8. Technical Constraints

Pulled directly from the seed doc / decisions made:
- Stack: [list]
- Infrastructure: [list]
- Local dev: [approach]
- Hard limits: [e.g. no paid services]

---

## 9. Success Metrics

How do we know the MVP worked?

| Metric | Target |
|---|---|
| ... | ... |

---

## 10. Open Questions

Things not yet decided that will block implementation:
- [ ] OQ-001: ...
- [ ] OQ-002: ...

---

## 11. Out of Scope (Post-MVP)

Features intentionally deferred:
- ...
```

---

## Rules

1. **No made-up requirements.** Every FR must trace to a user story. Every user story must trace to a stated goal.
2. **Acceptance criteria must be testable.** "Works correctly" is not an acceptance criterion.
3. **Flag assumptions clearly** with `⚠️ Assumed:` so the team can review.
4. **Keep user stories atomic.** One story = one discrete user action with one outcome.
5. **Respect constraints.** Never include requirements that contradict stated limits (budget, stack, team size).
6. **Number everything.** US-001, FR-001, OQ-001 — so `to-issues` can reference them directly.

---

## After Writing

Tell the user:
> "This PRD is ready to be converted to GitHub Issues using the `to-issues` skill. Run it with this PRD as input."

Flag any open questions that should be resolved before ticketing.