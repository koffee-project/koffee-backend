# Koffee Backend

[![Build Status](https://travis-ci.com/DerYeger/koffee-backend.svg?token=juB9bV6tFyoA5v7Hx1o4&branch=develop)](https://travis-ci.com/DerYeger/koffee-backend)

Documentation is available [here](https://koffee.yeger.eu/).

## Deployment

### Secrets

Secrets are stored in `.secret` files in the `secrets` directory and excluded from git.

#### koffee.secret

Configuration of the default admin and the secret string used by the HMAC algorithm for JWT verification and signing.

```
ID=koffee-admin-id
NAME=koffee-admin-name
PASSWORD=koffee-admin-password
HMAC_SECRET=yoursecretstring
```

### Development

1. Build the project using the Gradle `build` task.
2. Create the required secret.
3. Change the `URL` environment variable in `./environments/domain.env` as necessary.
4. Run `docker-compose up --build -d`.
5. The server is now accessible at `http://localhost:8080`.
6. Run `docker-compose down` to stop the server.

### Production

1. Build the project using the Gradle `build` task.
2. Create the required secret.
3. Change the `URL` environment variable in `./environments/domain.env` to a valid domain pointing to the host machine.
4. Ensure that ports 80 and 443 are forwarded to the host machine.
5. Run `docker-compose -f docker-compose-production.yml up --build -d`.
6. The server is now accessible at `https://your.domain/koffee`.
7. Run `docker-compose -f docker-compose-production.yml down` to stop the server.

## Database

The entire database can be dumped and restored using the following commands.\
Note: The parameter `koffee-database` needs to be changed if the name of the database has been modified in `koffee.env`.

Backup: `docker-compose exec -T mongo mongodump --archive --gzip --db koffee-database > dump.gz`\
Restore: `docker-compose exec -T mongo mongorestore --archive --gzip < dump.gz`

The following sections describe the schemas used by the MongoDB.
