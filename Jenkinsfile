pipeline {
    agent any

    environment {
        // Define repository URLs if needed, or assume checked out in workspace
        BACKEND_IMAGE_NAME = "ctso-backend"

        // Setup shared volume path if deploying via docker run
        HOST_UPLOAD_PATH = "/backup/home/ctso/uploads"
        CONTAINER_UPLOAD_PATH = "/app/uploads"
    }

    parameters {
        choice(name: 'DEPLOY_PROFILE', choices: ['dev', 'prod'], description: 'Select the Active Profile for Spring Boot')
    }

    stages {
        stage('Checkout') {
            steps {
                // Assuming Jenkins checks out the main repo (Backend) here.
                // If Frontend is a separate repo, you need to check it out specifically.
                // For this example, assuming directory structure:
                // workspace/CTSO (Backend)
                // workspace/CTSO_Master (Frontend)
                
                checkout scm
                
                // If Frontend is separate, you might need:
                // dir('../CTSO_Master') {
                //    git 'https://github.com/.../CTSO_Master.git'
                // }
                
                echo 'Checkout Complete'
            }
        }

        stage('Build Backend') {
            steps {
                dir('.') { // Backend Root
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Docker Build Backend') {
            steps {
                dir('.') {
                    script {
                        docker.build("${BACKEND_IMAGE_NAME}:${BUILD_NUMBER}")
                        docker.build("${BACKEND_IMAGE_NAME}:latest")
                    }
                }
            }
        }



        stage('Deploy') {
            steps {
                script {
                    sh 'docker stop ctso-backend || true'
                    sh 'docker rm ctso-backend || true'
                    // Map volume, port, and Active Profile
                    sh "docker run -d --restart unless-stopped --name ctso-backend -p 9013:8080 -v ${HOST_UPLOAD_PATH}:${CONTAINER_UPLOAD_PATH} -e SPRING_PROFILES_ACTIVE=${params.DEPLOY_PROFILE} ${BACKEND_IMAGE_NAME}:latest"
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
