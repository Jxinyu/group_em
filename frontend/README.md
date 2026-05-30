# Group EM Frontend

This module is the Vue 3 admin frontend for the Group Employee Management System.

## Features

- Dashboard-style management UI
- Employee, department, job, user, notice, document, and chat views
- Authentication flow and route guards
- Element Plus component-based interface
- API integration with the Spring Boot backend
- File download/export utilities

## Tech Stack

- Vue 3
- TypeScript
- Vite
- Element Plus
- Pinia
- Vue Router
- Axios
- VXE Table
- pnpm

## Structure

```text
frontend/
+-- public/
+-- src/
|   +-- api/
|   +-- assets/
|   +-- components/
|   +-- layout/
|   +-- router/
|   +-- store/
|   +-- styles/
|   +-- utils/
|   +-- views/
+-- package.json
+-- vite.config.ts
```

## Install

```powershell
pnpm install
```

## Development

```powershell
pnpm dev
```

The development API base path is configured in `.env.development`.

## Build

```powershell
pnpm build:prod
```

## Useful Scripts

- `pnpm dev`: start the Vite development server
- `pnpm build:stage`: build with staging mode
- `pnpm build:prod`: build for production
- `pnpm lint`: run linting and formatting
- `pnpm test`: run tests

## Notes

- Start the backend before using the frontend locally.
- Update the Vite proxy or `VITE_BASE_API` value if the backend address changes.
