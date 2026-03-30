pipeline {
    agent any

    environment {
        // [수정] 백엔드 명칭으로 변경
        DOCKER_IMAGE_NAME = "nanum-backend"

        // [유지] 요청하신 호스트 서버 실제 저장 경로
        HOST_UPLOAD_PATH = "/home/ttcc/nanum/upload"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'Nanum Backend Checkout Complete'
            }
        }

        // 백엔드는 빌드 과정이 필요하므로 이 단계만 유지합니다.
        stage('Build Backend') {
            steps {
                script {
                    sh "chmod +x gradlew"
                    sh "./gradlew clean build -x test"
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    sh """
                        docker build \
                            -t ${DOCKER_IMAGE_NAME}:${BUILD_NUMBER} \
                            -t ${DOCKER_IMAGE_NAME}:latest \
                            .
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh "docker stop ${DOCKER_IMAGE_NAME} || true"
                    sh "docker rm ${DOCKER_IMAGE_NAME} || true"

                    // [수정] 포트 9021, 백엔드용 볼륨 경로 적용
                    sh """
                        docker run -d \
                            --restart unless-stopped \
                            --name ${DOCKER_IMAGE_NAME} \
                            -p 9021:8080 \
                            -v ${HOST_UPLOAD_PATH}:/app/upload \
                            ${DOCKER_IMAGE_NAME}:latest
                    """
                }
            }
        }

        stage('Cleanup') {
            steps {
                sh "docker image prune -f"
            }
        }
    }

    post {
        success {
            echo "${DOCKER_IMAGE_NAME} 배포 성공 (포트: 9021, 볼륨: ${HOST_UPLOAD_PATH})"
        }
        failure {
            echo "${DOCKER_IMAGE_NAME} 빌드/배포 실패"
        }
    }
}