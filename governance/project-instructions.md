<!-- template-version: 2 -->
# Proyectos Project Instructions

## Core Principles

### I. Test-First

Implementation MUST begin with tests. A strict red-green-refactor cycle is mandatory for all new features. — Ensures functional correctness, prevents regressions, and enforces design for testability from the outset.

### II. Simplicity

Favor simple, readable, and maintainable solutions over complex abstractions (YAGNI). Avoid "just-in-case" engineering. — Minimizes technical debt, reduces maintenance cost, and accelerates onboarding for new contributors.

### III. Type Safety

Use strong, static typing for all project code where the language supports it. Avoid "any" or untyped data structures in critical paths. — Prevents entire classes of runtime errors and improves developer experience through enhanced IDE support and self-documenting code.

### IV. Agent Output Style

All agent output MUST be concise and outcome-oriented. This principle supersedes any verbose defaults.

- **Progress reports**: Facts and outcomes only — no narration, no restating the task.
- **Artifacts**: Emit required sections only — no preamble paragraphs, no summary epilogues.
- **Reasoning**: Omit unless the user asks "why" or the decision is non-obvious.
- **Errors / blockers**: State the problem, the attempted fix, and the result — nothing else.
- **Phase-boundary reports**: ≤ 5 bullet points.
- **Preserve without compressing**: Artifact template structure and required sections; explicit decision / registration / validation guidance in shared skills; delegation constraints and sub-agent role definitions; existing size limits (spec ≤ 10 KB, research ≤ 4 KB, stories ≤ 200 words).

## Technology Stack

- **Language/Runtime**: Java 21
- **Frameworks**: Spring Boot 4.0.2
- **Storage**: MySQL 8.x
- **Infrastructure**: Local (Gradle)

## Testing & Quality Policy

- **Coverage Target**: 100%
- **Required QC Categories**: linting, security scanning, coverage
- **Test Strategy**: Unit + integration; TDD mandatory
- **Linting / Formatting**: Checkstyle

## Source Code Layout

- **Policy**: ENFORCE_SRC_ROOT
- **Convention**: Source code lives under arrupeteca/src; tests co-located in __tests__/; config at repo root

## Development Workflow

- **Branching**: Feature branches from main, squash merge
- **Commit Convention**: Conventional Commits
- **CI Requirements**: All tests pass, lint clean, no type errors before merge

## Governance

- Project instructions supersede all other documentation and practices.
- Amendments require a version bump with ISO-dated changelog entry.
- All implementations MUST pass the Instructions Check gate during planning.
- Complexity beyond these principles MUST be justified and documented.

**Version**: 1.0.0 | **Last Amended**: 2026-03-31
