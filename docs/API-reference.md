# API Reference

This document provides an overview of all available REST API endpoints, their access permissions, and their purpose.

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| POST | `/api/auth/check-fin` | Public | Check whether a user exists and determine the authentication flow. |
| POST | `/api/auth/login` | Public | Authenticate a user and return a JWT token. |
| POST | `/api/auth/logout` | Public | Log out the current user. |
| POST | `/api/auth/register/verify` | Public | Verify the OTP during registration. |
| POST | `/api/auth/register/setup-password` | Public | Set the password after successful OTP verification. |
| GET | `/api/auth/me` | Authenticated | Retrieve information about the currently authenticated user. |
| POST | `/api/auth/register-new-user` | Authenticated | Register a new patient account. |

## Department Endpoints

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| GET | `/api/departments` | Public | Retrieve all departments. |
| GET | `/api/departments/{id}` | Public | Retrieve a department by its ID. |
| POST | `/api/departments` | Admin | Create a new department. |
| PUT | `/api/departments/{id}` | Admin | Update an existing department. |
| DELETE | `/api/departments/{id}` | Admin | Delete a department. |

## Doctor Endpoints

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| GET | `/api/doctors` | Public | Retrieve all doctors. |
| GET | `/api/doctors/{id}` | Public | Retrieve a doctor by ID. |
| GET | `/api/doctors/{id}/available-slots` | Public | Retrieve available appointment slots for a doctor. |
| POST | `/api/doctors` | Admin | Create a doctor profile. |
| PUT | `/api/doctors/{id}` | Admin, Doctor | Update a doctor profile. |
| PATCH | `/api/doctors/{id}/activate` | Admin | Activate a doctor account. |
| PATCH | `/api/doctors/{id}/deactivate` | Admin | Deactivate a doctor account. |

## Doctor Availability Endpoints

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| GET | `/api/availabilities` | Public | Retrieve all doctor availabilities. |
| GET | `/api/availabilities/{id}` | Public | Retrieve a specific availability. |
| GET | `/api/availabilities/doctor/{doctorId}` | Public | Retrieve all availabilities for a doctor. |
| POST | `/api/availabilities` | Admin, Doctor | Create a new availability. |
| PUT | `/api/availabilities/{id}` | Admin, Doctor | Update an availability. |
| PATCH | `/api/availabilities/{id}/status` | Admin, Doctor | Update availability status. |
| DELETE | `/api/availabilities/{id}` | Admin, Doctor | Delete an availability. |

## Appointment Endpoints

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| POST | `/api/appointments` | Patient | Create a new appointment. |
| GET | `/api/appointments` | Admin, Reception | Retrieve all appointments. |
| GET | `/api/appointments/{id}` | Admin, Patient, Doctor, Reception | Retrieve an appointment by ID. |
| GET | `/api/appointments/patient/{patientId}` | Admin, Patient, Reception | Retrieve appointments for a patient. |
| GET | `/api/appointments/doctor/{doctorId}` | Admin, Doctor, Reception | Retrieve appointments for a doctor. |
| PATCH | `/api/appointments/{id}/status` | Admin, Doctor, Reception | Update the appointment status. |

## Medical Record Endpoints

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| GET | `/api/records/{id}` | Admin, Patient, Doctor, Lab Technician | Retrieve a medical record by ID. |
| GET | `/api/records/patient/{patientId}` | Admin, Patient, Doctor, Lab Technician | Retrieve medical records for a patient. |
| POST | `/api/records` | Admin, Doctor | Create a medical record. |
| PUT | `/api/records/{id}` | Admin, Doctor | Update a medical record. |
| PATCH | `/api/records/{id}/status` | Admin, Doctor, Lab Technician | Update the medical record status. |