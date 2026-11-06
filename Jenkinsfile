
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Starting Git-Pulse pipeline'
                checkout scm
            }
        }

        stage('Build Backend Image') {
            steps {
                echo 'Building Git-Pulse backend'
                sh 'docker build -t git-pulse-backend:latest ./git-pulse-backend'
            }
        }

        stage('Build Frontend Image') {
            steps {
                echo 'Building Git-Pulse frontend'
                sh 'docker build -t git-pulse-frontend:latest ./git-pulse-ui'
            }
        }
    }

    post {
        success {
            echo 'Git-Pulse pipeline completed successfully!'
        }
        failure {
            echo 'Git-Pulse pipeline failed. Check the console output.'
        }
    }
}
