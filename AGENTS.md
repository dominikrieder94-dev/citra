# AGENTS

This file is the entry point for all agent work in this repository. Keep it short and keep linked docs up to date.

## Project description
This repository is the maintained local Citra fork. Treat it as the canonical working repo for Android-first recovery, releaseability, and future owner-requested features.

## Project goal
- Canonical goal: `.codex/docs/GOAL.md`
- If scope changes, update `.codex/docs/GOAL.md` first and record rationale in `.codex/docs/DECISIONS.md`.

## Engineering guidelines
- Prefer small, reviewable changes over broad speculative refactors.
- Keep emulator core, platform integration, release/bootstrap, and UI concerns separated.
- Preserve reproducibility: avoid undocumented local state, especially in `externals/`.
- Treat Android `arm64-v8a` device validation as the primary runtime target unless the owner says otherwise.

## Working files
- `.codex/docs/GOAL.md` - canonical project goals and success criteria
- `.codex/docs/CONCEPT.md` - architecture and implementation notes
- `.codex/docs/TASKS.md` - prioritized backlog and done items
- `.codex/docs/PROGRESS.md` - chronological work log
- `.codex/docs/HANDOVER.md` - newest-first verified state and next steps
- `.codex/docs/INSIGHTS.md` - discoveries, gotchas, and debugging notes
- `.codex/docs/DECISIONS.md` - ADR-style architecture and behavior decisions
- `.codex/docs/projects/` - optional project/workstream deep dives
- `.codex/skills/` - repo-local reusable skills
- `src/` - emulator and frontend source code
- `externals/` - third-party dependencies/submodules
- `dist/` - packaging/distribution assets

## Update rules
- When you start work: add a short intent entry to `.codex/docs/PROGRESS.md`.
- When you finish work: add outcomes and files touched to `.codex/docs/PROGRESS.md`.
- At session cutoff: add a concise entry to `.codex/docs/HANDOVER.md` with verified state and first next steps.
- After investigation or debugging: record findings in `.codex/docs/INSIGHTS.md` and note that in `.codex/docs/PROGRESS.md`.
- When making architecture or behavior decisions: log them in `.codex/docs/DECISIONS.md`.
- Keep `.codex/docs/TASKS.md` ordered by priority; move completed work to a Done section.
- When creating reusable workflows, preserve them in `.codex/skills/`.

## Documentation conventions
- Use plain English unless the owner requests otherwise.
- Keep entries concise and actionable.
- Use relative paths when referencing files.
- Do not delete historical logs; append follow-up corrections.

## Status
- Current date: 2026-03-15
- Owner: Dominik
