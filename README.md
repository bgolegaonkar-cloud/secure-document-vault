\# 🔐 Secure Document Vault



A full-stack secure document management system built with Spring Boot and React.

Documents are encrypted with AES-256 before storing in the database.



\## 🛠️ Technologies Used



\### Backend

\- Java 21 + Spring Boot 4

\- Spring Security + JWT Authentication

\- AES-256 File Encryption

\- BCrypt Password Hashing

\- MySQL + Spring Data JPA

\- Lombok



\### Frontend

\- React 18

\- Material UI (MUI)

\- Axios

\- React Router DOM



\## ✨ Features



\- ✅ JWT-based authentication (login/register)

\- ✅ AES-256 encryption for all uploaded files

\- ✅ BCrypt password hashing

\- ✅ Role-based access control (USER / ADMIN)

\- ✅ File upload and download with encryption/decryption

\- ✅ Audit logging — tracks all user actions

\- ✅ Admin panel with stats and audit logs

\- ✅ Beautiful responsive UI



\## 🚀 How to Run



\### Prerequisites

\- Java 21

\- Node.js

\- MySQL



\### Backend Setup

1\. Create MySQL database:

```sql

CREATE DATABASE vault\_db;

```

2\. Copy `application.properties.example` to `application.properties`

3\. Fill in your MySQL credentials and secret keys

4\. Run Spring Boot:

```bash

./mvnw spring-boot:run

```



\### Frontend Setup

```bash

cd frontend

npm install

npm start

```



\### Access

\- Frontend → http://localhost:3000

\- Backend → http://localhost:8080



\## 📡 API Endpoints



| Method | Endpoint | Access |

|--------|----------|--------|

| POST | /api/auth/register | Public |

| POST | /api/auth/login | Public |

| POST | /api/documents/upload | USER, ADMIN |

| GET | /api/documents/my-documents | USER, ADMIN |

| GET | /api/documents/download/{id} | USER, ADMIN |

| GET | /api/documents/admin/all | ADMIN only |

| GET | /api/documents/admin/audit-logs | ADMIN only |



\## 🔒 Security



\- All files encrypted with AES-256 before database storage

\- Passwords hashed with BCrypt (cost factor 10)

\- JWT tokens expire after 24 hours

\- Role-based API protection

\- CORS configured for frontend origin



\## 👤 Author

\- GitHub: \[@bgolegaonkar-cloud](https://github.com/bgolegaonkar-cloud)

