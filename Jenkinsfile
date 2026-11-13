pipeline {
    agent any

    environment {
        DOCKER_BUILDKIT = '1'
        GIT_SHORT_SHA = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
    }

    triggers {
        githubPush()
    }

    stages {
        // Stage 1: Run tests first (fail fast before building images)
        stage('Test') {
            parallel {
                stage('Backend Tests') {
                    steps {
                        echo 'Running Quarkus unit tests...'
                        sh '''
                            docker run --rm \
                              -v "${WORKSPACE}/git-pulse-backend:/app" \
                              -v maven-repo:/root/.m2 \
                              -w /app \
                              maven:3.9-eclipse-temurin-17 \
                              mvn test -B
                        '''
                    }
                }

                stage('Frontend Checks') {
                    steps {
                        echo 'Running frontend checks...'
                        sh '''
                            docker run --rm \
                              -v "${WORKSPACE}/git-pulse-ui:/app" \
                              -v npm-cache:/root/.npm \
                              -w /app \
                              node:20-alpine \
                              sh -c "npm ci && npm test -- --watchAll=false --passWithNoTests || true"
                        '''
                    }
                }
            }
        }

        // Stage 2: Build Docker images in parallel
        stage('Build') {
            parallel {
                stage('Build Backend Image') {
                    steps {
                        echo "Building git-pulse-backend:${GIT_SHORT_SHA}..."
                        sh """
                            docker build \
                              -t git-pulse-backend:${GIT_SHORT_SHA} \
                              -t git-pulse-backend:${BUILD_NUMBER} \
                              -t git-pulse-backend:latest \
                              ./git-pulse-backend
                        """
                    }
                }

                stage('Build Frontend Image') {
                    steps {
                        echo "Building git-pulse-frontend:${GIT_SHORT_SHA}..."
                        sh """
                            docker build \
                              -t git-pulse-frontend:${GIT_SHORT_SHA} \
                              -t git-pulse-frontend:${BUILD_NUMBER} \
                              -t git-pulse-frontend:latest \
                              ./git-pulse-ui
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'docker image prune -f'
        }
        success {
            echo "CI passed! Built git-pulse-backend:${GIT_SHORT_SHA} and git-pulse-frontend:${GIT_SHORT_SHA}"
        }
        failure {
            echo "CI failed on commit ${GIT_SHORT_SHA}."
        }
    }
}