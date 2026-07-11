# Authentication Documentation

The application uses **JWT (JSON Web Token)** for authentication and **role-based authorization (RBAC)**.

## Authentication Flow

### Existing User

1. The client sends the user's FIN to:

   ```
   POST /api/auth/check-fin
   ```

2. If the FIN exists and the user already has an account, the API indicates that the user should proceed to the login step.

3. The user enters their password and sends a request to:

   ```
   POST /api/auth/login
   ```

4. If the credentials are valid, the server authenticates the user and issues a JWT access token.

---

### First-Time User

1. The client sends the user's FIN to:

   ```
   POST /api/auth/check-fin
   ```

2. If the user does not yet have an account, the client continues with:

   ```
   POST /api/auth/register/verify
   ```

3. If the verification is successful, the client proceeds to:

   ```
   POST /api/auth/register/setup-password
   ```

4. The user creates a password, the account is activated, and future logins can be performed using the standard login endpoint.

---

## JWT Authentication

After a successful login, the server generates a **JWT access token**.

Instead of returning the token in the response body, it is stored in an **HttpOnly Cookie**.

### Why HttpOnly Cookies?

Using HttpOnly cookies improves application security because:

- JavaScript cannot access the token.
- Helps mitigate XSS (Cross-Site Scripting) attacks.
- The browser automatically sends the cookie with every authenticated request.
- The client application does not need to manually store or attach the JWT.

Because the cookie is managed by the browser, protected endpoints can authenticate users without requiring the client to manually include an `Authorization: Bearer <token>` header.

---

## Authorization

The application uses **Role-Based Access Control (RBAC)**.

Each authenticated user is assigned one or more roles.

Available roles include:

- ADMIN
- PATIENT
- DOCTOR
- RECEPTION
- LAB_TECHNICIAN

Access to protected endpoints is determined by these roles using Spring Security.

---

## Public Authentication Endpoints

| Method | Endpoint |
|---------|----------|
| POST | `/api/auth/check-fin` |
| POST | `/api/auth/login` |
| POST | `/api/auth/logout` |
| POST | `/api/auth/register/verify` |
| POST | `/api/auth/register/setup-password` |