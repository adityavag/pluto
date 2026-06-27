---
name: issues
description: >
  Converts a PRD or feature list into GitHub Issues autonomously — with labels,
  milestones, and structured descriptions. Trigger this whenever the user says
  "create tickets", "make GitHub issues", "convert PRD to issues", "create
  issues from this", "push to GitHub", or "ticket this up". Also trigger when
  the user has just finished a PRD (e.g. after using the `to-prd` skill) and
  wants to move into execution. This skill creates real issues via the GitHub
  CLI or API — not a list for the user to copy manually.
---

# To-Issues — PRD → GitHub Issues

You convert a PRD into a set of well-structured GitHub Issues and create them
autonomously using the GitHub CLI (`gh`) or GitHub REST API.

---

## Prerequisites

Check for GitHub CLI availability first:
```bash
gh auth status
```

If `gh` is available and authenticated → use CLI (preferred).  
If not → use the GitHub REST API with a token from the environment (`GITHUB_TOKEN`).  
If neither → ask the user to run `gh auth login` or provide a token.

---

## Step 1: Parse the PRD

Extract from the PRD:

| What | Maps to |
|---|---|
| Product name | GitHub milestone name |
| Feature areas | Labels (`area:auth`, `area:problems`, etc.) |
| User stories (US-xxx) | Parent issues or issue descriptions |
| Functional requirements (FR-xxx) | Individual GitHub Issues |
| Open questions (OQ-xxx) | Issues with label `question` |
| Non-functional requirements | Issues with label `non-functional` |

---

## Step 2: Plan the Issue Set

Before creating anything, output a plan in this format for the user to confirm:

```
## Issue Plan

**Repo:** [owner/repo]
**Milestone:** [product name] MVP

Labels to create:
- area:auth
- area:problems
- area:feedback
- type:feature
- type:bug
- type:question
- type:chore
- priority:high
- priority:medium
- priority:low

Issues to create ([N] total):
1. [TITLE] — [label(s)] — [milestone]
2. ...
```

Ask: "Shall I go ahead and create these in `[owner/repo]`?"

---

## Step 3: Create Labels

Create all labels first. Use consistent colours:

| Label prefix | Hex colour |
|---|---|
| `area:*` | `#0075ca` |
| `type:feature` | `#a2eeef` |
| `type:bug` | `#d73a4a` |
| `type:question` | `#d876e3` |
| `type:chore` | `#e4e669` |
| `priority:high` | `#b60205` |
| `priority:medium` | `#fbca04` |
| `priority:low` | `#0e8a16` |

```bash
gh label create "area:auth" --color "0075ca" --repo owner/repo
# repeat for each label
```

Skip labels that already exist (use `--force` flag or check first).

---

## Step 4: Create Milestone

```bash
gh api repos/owner/repo/milestones \
  --method POST \
  --field title="MVP" \
  --field description="Minimum viable product scope"
```

---

## Step 5: Create Issues

For each issue, use this template:

```bash
gh issue create \
  --repo owner/repo \
  --title "[FR-001] Set up user authentication" \
  --body "$(cat <<'EOF'
## User Story
> As a user, I want to sign up and log in so that my progress is saved.

**Story ref:** US-003  
**Requirement ref:** FR-001

## Description
[Clear description of what needs to be built]

## Acceptance Criteria
- [ ] User can sign up with email + password
- [ ] User can log in and receive a session token
- [ ] Invalid credentials return a 401 with a clear error message
- [ ] Passwords are hashed at rest (bcrypt)

## Technical Notes
- Use Clerk free tier for auth
- Session managed via JWT stored in httpOnly cookie

## Out of Scope
- OAuth / social login (post-MVP)
EOF
)" \
  --label "area:auth,type:feature,priority:high" \
  --milestone "MVP"
```

---

## Issue Body Template

Every issue must include:

```markdown
## User Story
> [Copied from PRD, or "N/A" for chores]

**Story ref:** US-XXX  
**Requirement ref:** FR-XXX

## Description
[What needs to be built, in plain language]

## Acceptance Criteria
- [ ] [Testable criterion 1]
- [ ] [Testable criterion 2]

## Technical Notes
[Stack-specific implementation hints, if known]

## Out of Scope
[What this issue explicitly does NOT cover]
```

---

## Step 6: Create Epic Issues (optional)

If the PRD has clear feature areas with 3+ issues each, create an Epic issue per area:

```markdown
## Epic: User Authentication

Tracks all auth-related issues for MVP.

### Issues
- [ ] #1 Set up Clerk integration
- [ ] #2 Protect API routes
- [ ] #3 User profile page
```

Label epics with `type:epic`.

---

## Step 7: Summary Report

After all issues are created, output:

```
## Done — [N] issues created in [owner/repo]

### By milestone
- MVP: [N] issues

### By area
- area:auth: [N]
- area:problems: [N]
- ...

### Open Questions
The following need answers before work can start:
- #[N]: OQ-001 — [title]

View all: https://github.com/owner/repo/issues
```

---

## Rules

1. **Confirm before creating.** Always show the plan and get approval first.
2. **One requirement = one issue.** Don't bundle multiple FRs into one ticket.
3. **Acceptance criteria must be checkboxes.** So they can be tracked in GitHub.
4. **Reference the PRD.** Every issue body must cite its US-xxx and FR-xxx.
5. **Don't create duplicate labels.** Check existing labels before creating.
6. **Keep titles short and scannable.** Max ~60 chars. Use `[FR-001]` prefix for traceability.
7. **Flag blockers.** If an issue depends on another, add `**Blocked by:** #N` to the body.