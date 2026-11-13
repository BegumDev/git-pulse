pipeline {
    agent any

    environment {
        DOCKER_BUILDKIT = '1'
    }

    stages {
        stage('Build Images') {
            parallel {
                stage('Build Backend Image') {
                    steps {
                        echo 'Building Git-Pulse backend'
                        sh 'docker build -t git-pulse-backend:${BUILD_NUMBER} -t git-pulse-backend:latest ./git-pulse-backend'
                    }
                }

                stage('Build Frontend Image') {
                    steps {
                        echo 'Building Git-Pulse frontend'
                        sh 'docker build -t git-pulse-frontend:${BUILD_NUMBER} -t git-pulse-frontend:latest ./git-pulse-ui'
                    }
                }
            }
        }
    }

    post {
        always {
            // Clean up untagged dangling images so your disk doesn't fill up
            sh 'docker image prune -f'
        }
        success {
            echo "Git-Pulse build #${BUILD_NUMBER} completed successfully!"
        }
        failure {
            echo "Git-Pulse build #${BUILD_NUMBER} failed. Check the console output."
        }
    }
}