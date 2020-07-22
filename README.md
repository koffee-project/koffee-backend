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
- password: String (Optional)

##### PUT /users

- id: String
- name: String
- isAdmin: Boolean
- password: String (Optional)

##### DELETE /users/:id

##### POST /users/:id/funding

- amount: Double

##### POST /items

- id: String
- name: String
- amount: Int (Optional)
- price: Double

##### PUT /items

- id: String
- name: String
- amount: Int (Optional)
- price: Double

##### DELETE /items/:id

## Secrets

Secrets are stored in `.secret` files in the `secrets` directory and excluded from git.

### koffee.secret

Configuration of the default admin and the secret string used by the HMAC algorithm for JWT verification and signing.

```
ID=koffee-admin-id
NAME=koffee-admin-name
PASSWORD=koffee-admin-password
HMAC_SECRET=yoursecretstring
```

## Deployment

### Development

1. Build the project using the Gradle `build` task.
2. Create the required secret.
3. Change the `URL` environment variable in `./environments/domain.env` as necessary.
4. Run `docker-compose build` and `docker-compose up -d`.
5. The server is now accessible at `http://localhost:8080`.
6. Run `docker-compose down` to stop the server.

### Production

1. Build the project using the Gradle `build` task.
2. Create the required secret.
3. Change the `URL` environment variable in `./environments/domain.env` to a valid domain pointing to the host machine.
4. Ensure that ports 80 and 443 are forwarded to the host machine.
5. Run `docker-compose -f docker-compose-production.yml build` and `docker-compose -f docker-compose-production.yml up -d`.
6. The server is now accessible at `https://your.domain/koffee`.
7. Run `docker-compose -f docker-compose-production.yml down` to stop the server.

## Database

The entire database can be dumped and restored using the following commands.\
Note: The parameter `koffee-database` needs to be changed if the name of the database has been modified in `koffee.env`.

Backup: `docker-compose exec -T mongo mongodump --archive --gzip --db koffee-database > dump.gz`\
Restore: `docker-compose exec -T mongo mongorestore --archive --gzip < dump.gz`

The following sections describe the schemas used by the MongoDB.

### User

- id: String
- name: String
- isAdmin: Boolean
- password: String (Nullable, only present if isAdmin is true)
- transactions: TransactionList

### TransactionList

- transactions: List\<Transaction>

### Profile Image

- id: String
- encodedImage: String
- timestamp: String

### Item

- id: String
- name: String
- amount: Int (Nullable)
- price: Double

### Transactions

There are three types of transactions. The type field is required for polymorphism.

#### Funding

- type: String (Is always "funding")
- value: Double
- timestamp: Long

#### Purchase

- type: String (Is always "purchase")
- value: Double
- timestamp: Long
- itemId: String
- itemName: String
- amount: Int

#### Refund

- type: String (Is always "refund")
- value: Double
- timestamp: Long
- itemId: String
- itemName: String
- amount: Int

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
