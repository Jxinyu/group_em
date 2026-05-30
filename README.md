# Group Employee Management System

Group EM is a full-stack management system. The repository combines the Java backend and Vue frontend in one project.

## Features

- Employee, department, job, document, and notice management
- Private chat and group chat modules
- File and notice image upload support
- Email notification and group email support
- Face search integration through Aliyun Facebody
- JWT-based authentication utilities
- Vue 3 admin dashboard with Element Plus

## Tech Stack

Backend:

- Java 8
- Spring Boot
- MyBatis-Plus
- PageHelper
- Apache Shiro
- MySQL
- Maven

Frontend:

- Vue 3
- TypeScript
- Vite
- Element Plus
- Pinia
- Vue Router
- pnpm

## Project Structure

```text
group_em/
+-- backend/     Spring Boot backend
+-- frontend/    Vue 3 admin frontend
+-- README.md
```

## Backend Configuration

Sensitive values are read from environment variables.

| Variable | Description | Default |
| --- | --- | --- |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | empty |
| `DB_URL` | JDBC connection URL | local `csi` database |
| `MAIL_USERNAME` | SMTP username | empty |
| `MAIL_PASSWORD` | SMTP password or authorization code | empty |
| `JWT_SECRET` | JWT signing secret | `change-me` |
| `ALIYUN_ACCESS_KEY_ID` | Aliyun AccessKey ID for face search | required for face search |
| `ALIYUN_ACCESS_KEY_SECRET` | Aliyun AccessKey Secret for face search | required for face search |

## Run Locally

Backend:

```powershell
cd backend
.\mvnw spring-boot:run
```

Frontend:

```powershell
cd frontend
pnpm install
pnpm dev
```

The backend runs on port `8080` by default. The frontend uses Vite development settings from `frontend/.env.development`.

## Notes

- Runtime secrets are intentionally not committed to the repository.
- Database schema, seed data, upload directories, and third-party service credentials must be prepared locally.
- The previous standalone frontend and backend repositories have been merged here under `frontend/` and `backend/`.
