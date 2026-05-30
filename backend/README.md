# Group EM Backend

This module is the Spring Boot backend for the Group Employee Management System.

## Features

- Employee, department, job, notice, document, and user management APIs
- Private chat and group chat backend support
- File upload and notice image upload configuration
- Email and group email services
- Aliyun Facebody-based face search utility
- Authentication utilities based on Shiro and JWT
- MyBatis-Plus mapper and XML-based persistence layer

## Tech Stack

- Java 8
- Spring Boot
- Apache Shiro
- MyBatis-Plus
- PageHelper
- MySQL
- Maven

## Structure

```text
backend/
+-- src/main/java/com/gp
|   +-- config/
|   +-- controller/
|   +-- filter/
|   +-- mapper/
|   +-- pojo/
|   +-- service/
|   +-- utils/
|   +-- ws/
+-- src/main/resources
|   +-- mapper/
|   +-- static/
|   +-- application.yml
+-- pom.xml
```

## Configuration

Set these environment variables before running the service:

| Variable | Description |
| --- | --- |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `DB_URL` | JDBC connection URL |
| `MAIL_USERNAME` | SMTP account username |
| `MAIL_PASSWORD` | SMTP password or authorization code |
| `JWT_SECRET` | JWT signing secret |
| `ALIYUN_ACCESS_KEY_ID` | Aliyun AccessKey ID |
| `ALIYUN_ACCESS_KEY_SECRET` | Aliyun AccessKey Secret |

PowerShell example:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="your_jwt_secret"
$env:ALIYUN_ACCESS_KEY_ID="your_aliyun_key_id"
$env:ALIYUN_ACCESS_KEY_SECRET="your_aliyun_key_secret"
```

## Run

```powershell
.\mvnw spring-boot:run
```

The backend listens on port `8080` by default.

## Notes

- Runtime secrets are not committed to the repository.
- Prepare the MySQL schema and seed data before starting the application.
- Local upload paths are configured in `src/main/resources/application.yml`.
