# API Gateway

The gateway is the only public entry point for the User and Product services. It runs on port `8080` and discovers services through Eureka.

## Required configuration

For local development, all services share a built-in fallback secret. For deployment, set the same unique 32-character-or-longer secret in User Service, Product Service, and Gateway. Base64 is supported but not required:

```powershell
$env:JWT_SECRET = "replace-with-a-unique-secret-of-at-least-32-characters"
```

Replace this example value with a unique production secret. Use the exact same value for all three services.

## Start order

1. Start Eureka Server on `8761`.
2. Start User Service on `8081` with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` configured.
3. Start Product Service on `8082` with `MONGO_URI` and `JWT_SECRET` configured.
4. Start this Gateway on `8080`.

Use Maven from this folder:

```powershell
mvn spring-boot:run
```

## Public endpoints

- `POST /api/users/register`
- `POST /api/auth/login`
- `GET /actuator/health`

All product endpoints require an `Authorization: Bearer <token>` header.

## Product authorization

- `USER`: `GET /api/products`, `GET /api/products/{id}`
- `SELLER`: user access plus `POST /api/products`, `POST /api/products/batch`, `PUT /api/products/{id}`, and `DELETE /api/products/{id}` for products they own.
- `ADMIN`: all product operations, including any seller's products.
