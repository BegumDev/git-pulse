## CI/CD Pipeline

This repository uses **Jenkins** for automated Docker image builds on every push to `main`.

### Architecture
- **Backend (`git-pulse-backend`):** Quarkus (Java 17) packaged as an uber-jar and served via `eclipse-temurin:17-jre-alpine` on port `8080`.
- **Frontend (`git-pulse-ui`):** React static bundle built with Node 20 and served via `nginx:alpine` on port `80`.
- **Automation:** GitHub webhooks trigger the Jenkins pipeline via an ngrok tunnel, building both images in parallel with Docker BuildKit enabled.

### Local Build Commands
```bash
# Build backend image
docker build -t git-pulse-backend:latest ./git-pulse-backend

# Build frontend image
docker build -t git-pulse-frontend:latest ./git-pulse-ui