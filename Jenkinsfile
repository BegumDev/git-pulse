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
        // Stage 1: Run backend unit tests via Docker build target
        stage('Test') {
            steps {
                echo 'Running Quarkus unit tests...'
                sh 'docker build --target test -t git-pulse-backend:test ./git-pulse-backend'
            }
        }

        // Stage 2: Build final production images in parallel
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