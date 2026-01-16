Render deployment notes

This project uses a multi-stage Dockerfile that builds the application inside a Maven+Temurin builder image, then copies the produced JAR into an Amazon Corretto runtime image. This avoids requiring a pre-built `target/*.jar` file and fixes the `lstat /target: no such file or directory` error you saw on Render.

Quick steps to deploy to Render

1. Commit & push your changes (including the updated `Dockerfile`) to the branch you use for Render deploys.

   git add Dockerfile
   git commit -m "Use multi-stage Dockerfile with Temurin builder; limit Maven RAM/parallelism"
   git push origin develop

2. Trigger a deploy on Render (Render will detect the new commit and run `docker build`).

3. Watch the Render deploy logs. If the build fails, copy the last ~200 lines of the deploy log and paste them here so I can help.

Verify the running app

- If the app starts successfully, you should be able to access the service at the URL Render provides. You can also check:
  - Health endpoint (if actuator enabled): `GET /actuator/health`
  - Swagger UI: `/swagger-ui/index.html` or `/swagger-ui.html` depending on Springdoc version

Common issues & remedies

- Build fails with OOM / killed process

  - The Dockerfile sets `ENV MAVEN_OPTS="-Xmx1024m -XX:MaxMetaspaceSize=256m"` and uses `-T 1C` to reduce memory during the build. If Render still kills the process, try lowering `-Xmx` or use a Render build command to pre-build the JAR and use a runtime-only Dockerfile.

- Network/dependency timeouts

  - Rare on Render, but possible. Retry deploy. If you have private/internal dependencies, ensure Render has access or use a private registry / artifact repository.

- Database connectivity
  - If you rely on an external Postgres (Neon), make sure database host/credentials are reachable from Render. For local dev, use the `docker-compose.override.yml` which defines a `db` service and adjusts `SPRING_DATASOURCE_URL` to `jdbc:postgresql://db:5432/smart_service_booking`.

If you want me to do more

- I can add a `README.md` section showing exact Render settings (build command, health check) and a sample `render.yaml` if you use Render's native manifest.
- I can also prepare a runtime-only Dockerfile variant and a CI script that pre-builds the jar and pushes a runtime image. Tell me which you prefer.

---

Verification performed locally

- Built the Docker image locally with: `docker build --no-cache -t ssb-render-test .` and it completed successfully. The multi-stage build performed the Maven package step inside the image.

If you'd like, I can now: (A) produce a runtime-only Dockerfile and instructions, (B) produce a `render.yaml` manifest, or (C) wait while you push and redeploy and then analyze logs if any failure occurs. Which do you prefer?
