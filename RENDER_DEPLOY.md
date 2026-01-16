Render deployment quick guide

This repo now contains:

- `Dockerfile` — multi-stage Dockerfile that builds the JAR inside a Maven+Temurin builder and packages into an Amazon Corretto runtime image.
- `render.yaml` — example Render manifest (edit `repo`, and DO NOT include secrets in the file).

Steps to deploy on Render

1. Commit & push everything to your repo and branch (e.g., `develop`):

```bash
git add Dockerfile render.yaml
git commit -m "Add multi-stage Dockerfile and Render manifest"
git push origin develop
```

2. Create a new Web Service on Render (or use Deploy from Manifest):

   - Provider: GitHub (connect your account)
   - Repository: select `Smart-Service-Booking`
   - Branch: `develop` (or whichever branch you pushed)
   - Environment: Docker
   - Render will read `render.yaml` if you choose "From manifest"; otherwise just create normally and Render will run `docker build`.

3. Add environment variables in Render dashboard (Service → Environment → New Environment Variable):

   - `SPRING_DATASOURCE_URL` — e.g. `jdbc:postgresql://<host>:5432/<db>?sslmode=require`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` (if using Cloudinary)
   - Optional: `SPRING_JPA_HIBERNATE_DDL_AUTO` (e.g., `update` or `none`) and `SPRING_JPA_DATABASE_PLATFORM` if you want to override dialect.

4. Trigger a deploy and watch logs.
   - If build fails during the Maven stage, check for OOM / "Killed" messages. We already added `MAVEN_OPTS` and limited Maven parallelism in `Dockerfile`.
   - If the app fails at startup with DB errors, paste the last ~100 lines of the deploy logs here and I'll investigate.

Useful Render log checks

- Build stage errors: look for the `mvn` output and any `OutOfMemoryError` or dependency download failures.
- Startup errors: look for `Failed to initialize JPA EntityManagerFactory` or `Driver org.h2.Driver claims to not accept jdbcUrl` (this was fixed by allowing driver auto-detection in `application.properties`).

Optional improvements I can make

- Add `spring-boot-starter-actuator` to provide `/actuator/health` health checks.
- Add a GitHub Actions workflow that runs `mvn -DskipTests package` and builds/pushes a runtime-only Docker image to a container registry, then Render (or your infra) pulls that image.

If you want, I can create the GitHub Actions workflow and a runtime-only Dockerfile next. Which one should I do?
