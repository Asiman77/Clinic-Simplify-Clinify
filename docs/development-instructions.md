# Development Instructions

## Branch Strategy

Create a feature branch before starting any development.

Example:

```bash
git checkout -b feature/add-appointment-api
```

## Commit Convention

Follow Conventional Commits.

Examples:

```text
feat: add appointment creation endpoint
fix: resolve JWT authentication issue
docs: update API documentation
refactor: simplify appointment service
```

## Running Tests

```bash
mvn test
```

## Code Style

- Follow Java naming conventions.
- Use constructor injection.
- Validate request DTOs.
- Keep controllers thin.
- Place business logic inside service classes.
- Document public APIs using Swagger annotations.

## Pull Requests

Before creating a Pull Request:

- Ensure the project builds successfully.
- Run all tests.
- Update documentation if necessary.
- Request at least one code review.

## API Documentation

After implementing or modifying an endpoint, update:

- `docs/APIreference.md`
- Swagger annotations (if applicable)

## Environment

Never commit:

- `.env`
- API keys
- JWT secrets
- Database credentials