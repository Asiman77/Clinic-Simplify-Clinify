## Environment Variables

The application requires the following environment variables:

| Variable | Description |
|----------|-------------|
| `DB_URL` | MySQL database connection URL (development). |
| `DB_USERNAME` | MySQL database username (development). |
| `DB_PASSWORD` | MySQL database password (development). |
| `JWT_SECRET` | Secret used to sign and verify JWT tokens. |
| `SMS_API_KEY` | API key used for OTP SMS delivery. |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name. |
| `CLOUDINARY_API_KEY` | Cloudinary API key. |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret. |

> **Production profile:** Database credentials are provided through the standard Spring Boot datasource environment variables configured by the hosting platform.