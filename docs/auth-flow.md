# Authentication Documentation

The application uses **JWT (JSON Web Token)** for authentication and **Role-Based Access Control (RBAC)**.

---

# Authentication Flow

The authentication process is based on whether the user has already activated an account.

## Step 1 – Check FIN

Every authentication request starts by checking the user's FIN.

### Endpoint

```http
POST /api/auth/check-fin
```

### Request

```json
{
  "fin": "GG7777G"
}
```

---

### Case 1 – Existing User

If the user already has an account (`hasAccount = true`), the API returns:

```json
{
  "fin": "GG7777G",
  "status": "LOGIN_REQUIRED",
  "message": "Please insert password"
}
```

The client should navigate to the **Login** screen.

---

### Case 2 – First-Time User

If the user exists but has not yet activated their account (`hasAccount = false`), the API returns:

```json
{
  "fin": "GG7777G",
  "status": "REGISTER_REQUIRED",
  "message": "You are not registered. Please insert signature."
}
```

The client should navigate to the OTP verification step.

---

# Existing User Login

After receiving the `LOGIN_REQUIRED` status, the client prompts the user to enter their password.

### Endpoint

```http
POST /api/auth/login
```

### Request

```json
{
  "fin": "GG7777G",
  "password": "StrongPassword123"
}
```

### Response

```json
{
  "fin": "GG7777G",
  "roles": [
    "ROLE_PATIENT"
  ]
}
```

> **Note**
>
> Although the response object contains a JWT token internally, it is hidden from the JSON response using `@JsonIgnore`.

---

### Authentication Cookie

After successful authentication, the server creates an **HttpOnly JWT Cookie**.

Example response header:

```http
Set-Cookie:
token=<JWT_TOKEN>;
HttpOnly;
Path=/;
Max-Age=3600;
SameSite=Lax
```

The browser stores this cookie automatically and includes it in every subsequent request.

---

# First-Time User Registration

If the previous step returned `REGISTER_REQUIRED`, the client continues with OTP verification.

### Endpoint

```http
POST /api/auth/register/verify
```

### Request

```json
{
  "fin": "GG7777G"
}
```

### Successful Response

```json
{
  "fin": "GG7777G",
  "verified": true,
  "status": "SETUP_PASSWORD_REQUIRED",
  "message": "OTP verification successful."
}
```

When `verified` is **true**, the client redirects the user to the password setup screen.

---

# Password Setup

After successful OTP verification, the user creates a password.

### Endpoint

```http
POST /api/auth/register/setup-password
```

### Request

```json
{
  "fin": "GG7777G",
  "password": "StrongPassword123"
}
```

### Response

```text
Password successfully created.
```

Once the password is created:

- the user's account becomes active;
- `hasAccount` is set to `true`;
- future authentication is performed using the normal login endpoint.

---

# Current Authenticated User

Returns information about the currently authenticated user.

### Endpoint

```http
GET /api/auth/me
```

### Response

```json
{
  "id": 15,
  "fin": "GG7777G",
  "firstName": "John",
  "lastName": "Doe",
  "roles": [
    "ROLE_PATIENT"
  ]
}
```

> The exact response depends on the `UserResponse` model.

---

# Logout

Removes the authentication cookie.

### Endpoint

```http
POST /api/auth/logout
```

### Response

```http
204 No Content
```

The server expires the authentication cookie.

Example:

```http
Set-Cookie:
token=;
Max-Age=0;
HttpOnly;
Path=/;
SameSite=Lax
```

---

# Reception Registration

Accessible only to users with the **RECEPTION** role.

### Endpoint

```http
POST /api/auth/register-new-user
```

### Request

```json
{
  "fin": "AA1234A",
  "firstName": "John",
  "lastName": "Doe",
  "birthDate": "1998-05-18",
  "gender": "MALE"
}
```

### Response

```text
Patient successfully registered.
```

---

# Why HttpOnly Cookies?

The application stores JWT tokens inside **HttpOnly Cookies** instead of exposing them to JavaScript.

Benefits:

- Prevents JavaScript from accessing the authentication token.
- Reduces the impact of XSS (Cross-Site Scripting) attacks.
- The browser automatically sends the cookie with authenticated requests.
- The frontend does not need to manually manage JWT storage.
- Eliminates the need to send an `Authorization: Bearer <token>` header for every request.

---

# Authorization

The application uses **Role-Based Access Control (RBAC)**.

Available roles:

- ADMIN
- PATIENT
- DOCTOR
- RECEPTION
- LAB_TECHNICIAN

Access to protected resources is controlled using Spring Security based on the authenticated user's assigned roles.