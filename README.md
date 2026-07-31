# Distributed Systems - FoodNutri

## What this is

This project runs the FoodNutri services with Docker.

Services:

- EurekaServer
- ApiGateway
- TestService / Food service
- ProfileService
- ChallengeService
- Keycloak (identity infrastructure)

The application microservices remain separate. Keycloak only replaces the
password and session handling that previously lived in ProfileService.

## Start with Docker

From the project root:

```powershell
docker compose up --build
```

If it was already built before:

```powershell
docker compose up
```

Run in background:

```powershell
docker compose up -d
```

Stop everything:

```powershell
docker compose down
```

Check containers:

```powershell
docker compose ps
```

## URLs

Main app through the gateway:

```text
http://localhost:8085
```

Login:

```text
http://localhost:8085/login
```

Food log:

```text
http://localhost:8085/food
```

Challenges:

```text
http://localhost:8085/challenge-ui
```

Eureka dashboard:

```text
http://localhost:8761
```

## Gateway

The gateway is the main entry point.

Instead of opening every service on its own port, use:

```text
http://localhost:8085
```

Routes:

- `/` and `/login` go to ProfileService
- `/food` goes to TestService
- `/challenge-ui` goes to ChallengeService
- `/api/foods/**` goes to TestService
- `/api/profiles/**` goes to ProfileService
- `/api/challenges/**` goes to ChallengeService

## Notes

After starting Docker, wait a bit until all services are registered in Eureka.

## Keycloak authentication

Keycloak runs at:

```text
http://localhost:8090
```

The development realm, browser client and role are imported from
`keycloak/foodnutri-realm.json` when Keycloak starts for the first time.

Development administrator:

| Purpose | Username | Password |
|---|---|---|
| Keycloak admin console | `admin` | `admin` |

These credentials and Keycloak's `start-dev` mode are only intended for local
development. Change them before using the project outside a local machine.

The browser uses the OpenID Connect Authorization Code flow with PKCE. The
Access Token is sent as `Authorization: Bearer <token>`. ProfileService,
TestService and ChallengeService each validate the signed JWT independently.
ChallengeService forwards the same token when it calls TestService.

ProfileService no longer stores passwords. `GET /api/profiles/me` creates or
loads the local application profile using Keycloak's stable `sub` user ID.

## Test the complete login flow

1. Start Docker Desktop.
2. From the repository root, start the stack:

   ```powershell
   docker compose up --build
   ```

3. Wait until Keycloak reports that it has started and the application services
   appear in Eureka:

   ```powershell
   docker compose ps
   docker compose logs keycloak
   ```

4. Open `http://localhost:8085/login`.
5. Select **Konto erstellen**, create your own local account and sign in.
6. Add or search for a food and open the challenge page. Both APIs now require
   the Keycloak token.
7. Select **Logout** on the food page. This ends the Keycloak session and removes
   the browser tokens.

The Keycloak administration console is available at
`http://localhost:8090/admin` with `admin` / `admin`.

## Test API protection directly

A request without a token must return `401 Unauthorized`:

```powershell
curl.exe -i http://localhost:8085/api/foods
curl.exe -i -H "Authorization: Bearer invalid" http://localhost:8085/api/foods
```

To test a valid token, log in in the browser and copy
`foodnutriProfileToken` from **Developer Tools > Application > Session Storage >
http://localhost:8085**. Then run:

```powershell
$token = "PASTE_ACCESS_TOKEN_HERE"
curl.exe -i -H "Authorization: Bearer $token" http://localhost:8085/api/profiles/me
curl.exe -i -H "Authorization: Bearer $token" http://localhost:8085/api/foods
curl.exe -i -H "Authorization: Bearer $token" http://localhost:8085/api/challenges
```

The three requests should return `200 OK`.

## Run automated tests

The security tests verify unauthenticated (`401`) and authenticated requests.
Run them from each changed service directory:

```powershell
cd ProfileService
.\gradlew.bat test

cd ..\TestService
.\gradlew.bat test

cd ..\ChallengeService
.\gradlew.bat test
```

If the imported realm must be recreated, the Keycloak development volume can be
removed with `docker compose down -v`. This deletes the locally stored Keycloak
users and realm state; the realm is imported again on the next start.
