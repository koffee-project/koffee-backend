# Koffee Backend

<p align="center">
    <a href="https://www.apache.org/licenses/LICENSE-2.0"><img alt="License" src="https://img.shields.io/github/license/koffee-project/koffee-backend?color=e9d0b9&style=for-the-badge"></a>
    <a href="https://travis-ci.com/koffee-project/koffee-backend"><img alt="Build" src="https://img.shields.io/travis/com/deryeger/refunk?color=a9755c&style=for-the-badge"></a>
    <a href="https://codecov.io/gh/koffee-project/koffee-backend"><img alt="Coverage" src="https://img.shields.io/codecov/c/github/koffee-project/koffee-backend?color=b4534b&style=for-the-badge"></a>
    <a href="https://koffee.yeger.eu"><img alt="Documentation" src="https://img.shields.io/badge/Documentation-Available-blue?color=bc9a7c&style=for-the-badge"></a>
</p>

> Backend for a digital kitty.

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
