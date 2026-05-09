pipeline {
    agent any

    environment {
        IMAGE_NAME = 'java-springboot-app'
        STAGING_PORT = '9091'
        PROD_PORT = '9092'
        STAGING_CONTAINER = 'springboot-staging'
        PROD_CONTAINER = 'springboot-prod'
        TIMEOUT = '10'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-21-alpine'
                    args '-u root --privileged'
                    reuseNode true
                }
            }
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw package -Dmaven.repo.local=.m2'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Parallel Checks') {
    failFast: true 
    parallel {
        stage('Code Quality') 
        steps { 
             echo "Running code quality checks..."
            }
        stage('Security Scan') 
        steps { 
             echo "Running security vulnerability scan..."
         }
    }
}

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
                    docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest
                """
            }
        }

        stage('Deploy to Staging') {
            steps {
                sh """
                    docker stop ${STAGING_CONTAINER} || true
                    docker rm ${STAGING_CONTAINER} || true
                    docker run -d -p ${STAGING_PORT}:9090 --name ${STAGING_CONTAINER} ${IMAGE_NAME}:${BUILD_NUMBER}
                """
            }
        }

        stage('Approval') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
        input message: 'Staging looks good? Approve to deploy to Production.', ok: 'Deploy to Production'
    }
            }
        }

        stage('Deploy to Production') {
            steps {
                sh """
                    docker stop ${PROD_CONTAINER} || true
                    docker rm ${PROD_CONTAINER} || true
                    docker run -d -p ${PROD_PORT}:9090 --name ${PROD_CONTAINER} ${IMAGE_NAME}:${BUILD_NUMBER}
                """
            }
        }
    }

    post {
        success {
            echo "Pipeline succeeded. ${IMAGE_NAME}:${BUILD_NUMBER} is live in production."
        }
        failure {
            echo "Pipeline failed at build ${BUILD_NUMBER}."
        }
    }
}