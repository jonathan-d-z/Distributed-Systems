# Distributed Systems - FoodNutri

## What this is

This project runs the FoodNutri services with Docker.

Services:

- EurekaServer
- ApiGateway
- TestService / Food service
- ProfileService
- ChallengeService

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

There is no default account anymore. Create an account on the login page first.


