# koffee-backend

[![Build Status](https://travis-ci.com/DerYeger/koffee-backend.svg?token=juB9bV6tFyoA5v7Hx1o4&branch=develop)](https://travis-ci.com/DerYeger/koffee-backend)

Documentation is available at http://koffee-backend.yeger.eu/.

## Routes

All routes use JSON for content delivery.

### Without authentication

##### POST /login

- id: String
- password: String

##### GET /users

##### GET /users/:id

##### POST /users/:id/purchases

- itemId: String
- amount: Int

##### POST /users/:id/purchases/refund

- No body required.

##### GET /users/:id/image

##### GET /users/:id/image/timestamp

##### POST /users/:id/image

- Image file as multipart body.

##### DELETE /users/:id/image

##### GET /items

##### GET /items/:id

### With authentication

##### POST /users

- id: String
- name: String
- isAdmin: Boolean
- password: String (optional)

##### PUT /users

- id: String
- name: String
- isAdmin: Boolean
- password: String (optional)

##### DELETE /users/:id

##### POST /users/:id/funding

- amount: Double

##### POST /items

- id: String
- name: String
- amount: Int (optional)
- price: Double

##### PUT /items

- id: String
- name: String
- amount: Int (optional)
- price: Double

##### DELETE /items/:id

## Secrets

Secrets are stored in `.secret` files in the `secrets` directory and excluded from git.

### default_admin.secret

Configuration of the default admin as seen below.

```
ID=koffee-admin-id
NAME=koffee-admin-name
PASSWORD=koffee-admin-password
```

### hmac_secret.secret

The secret string used by the HMAC algorithm for JWT verification and signing.

## Deployment

### Development

1. Build the project using the Gradle `build` task.
2. Create the required secrets.
3. Change the `URL` environment variable in `./environments/domain.env` as necessary.
4. Run `docker-compose build` and `docker-compose up -d`.
5. The server is now accessible at `http://localhost:8080`.
6. Run `docker-compose down` to stop the server.

### Production

1. Build the project using the Gradle `build` task.
2. Create the required secrets.
3. Change the `URL` environment variable in `./environments/domain.env` to a valid domain pointing to the host machine.
4. Ensure that ports 80 and 443 are forwarded to the host machine.
5. Run `docker-compose -f docker-compose-production.yml build` and `docker-compose -f docker-compose-production.yml up -d`.
6. The server is now accessible at `https://your.domain/koffee`.
7. Run `docker-compose -f docker-compose-production.yml down` to stop the server.

## Timetable

- [x] 06.04.2020 – VCS-Setup, Grundlage Backend (Datenbankanbindung, Docker)
- [x] 13.04.2020 – Aufbau und Struktur des Backends
- [x] 04.05.2020 – Authentifizierung von Administratoren beim Backend
- [x] 11.05.2020 – Endpunkte zum Erstellen, Aktualisieren und Löschen von Artikeln bauen
- [x] 18.05.2020 – Aufbau und Struktur der App
- [X] 25.05.2020 – Mockups der Benutzeroberflächen
- [x] 01.06.2020 – Endpunkt „Guthaben aufladen“ bauen 
- [X] 08.06.2020 – Bildschirm „Administratorfunktionen“ bauen
- [x] 22.06.2020 – Endpunkte „Nutzerliste“, „Kaufen“ und „Stornieren“ bauen
- [x] 06.07.2020 – Bildschirm „Nutzerauswahl“, „Artikelliste“ und „Kaufen“ bauen
- [ ] 13.07.2020 – Erster Testlauf mit Feedbackphase
- [ ] 27.07.2020 – Umsetzung des Feedbacks und optionale Features
- [ ] 03.08.2020 – Zweiter Testlauf
- [ ] 17.08.2020 – Fertigstellung von App und Backend
- [ ] 14.09.2020 – Dokumentation schreiben
- [ ] 21.09.2020 – Präsentation vorbereiten
- [ ] 30.09.2020 – Abgabetermin und finaler Vortrag
