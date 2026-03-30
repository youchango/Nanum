pipeline {
    agent any

    environment {
        // [변경] ctso-backend -> nanum-backend (Nanum 브랜드 통일)
        BACKEND_IMAGE_NAME = "nanum-backend"

        // 업로드 파일 볼륨 공유 경로 설정
        HOST_UPLOAD_PATH = "/backup/home/ctso/uploads"
        CONTAINER_UPLOAD_PATH = "/app/uploads"
    }

    parameters {
        // 배포 환경 선택 파라미터 (dev/prod)
        choice(name: 'DEPLOY_PROFILE', choices: ['dev', 'prod'], description: 'Spring Boot 실행 프로파일 선택')
    }

    stages {
        stage('Checkout') {
            steps {
                // 현재 레포지토리(Nanum Backend) Checkout
                checkout scm
                echo 'Nanum Backend Checkout Complete'
            }
        }

        stage('Build Backend') {
            steps {
                dir('.') {
                    // Gradle Clean Build (테스트 제외)
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Docker Build Backend') {
            steps {
                dir('.') {
                    script {
                        // 빌드 번호 태그 및 latest 태그로 이미지 생성
                        docker.build("${BACKEND_IMAGE_NAME}:${BUILD_NUMBER}")
                        docker.build("${BACKEND_IMAGE_NAME}:latest")
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // [변경] 컨테이너명: ctso-backend -> nanum-backend
                    sh 'docker stop nanum-backend || true'
                    sh 'docker rm nanum-backend || true'

                    // [변경] 포트: 9013:8080 -> 9021:8080
                    // 볼륨 마운트, 포트, Spring Profile 주입
                    sh """
                        docker run -d \
                            --restart unless-stopped \
                            --name nanum-backend \
                            -p 9021:8080 \
                            -v ${HOST_UPLOAD_PATH}:${CONTAINER_UPLOAD_PATH} \
                            -e SPRING_PROFILES_ACTIVE=${params.DEPLOY_PROFILE} \
                            ${BACKEND_IMAGE_NAME}:latest
                    """
                }
            }
        }
    }

    post {
        always {
            // 빌드 완료 후 워크스페이스 정리
            cleanWs()
        }
        success {
            echo "nanum-backend 배포 성공 (포트: 9021, 프로파일: ${params.DEPLOY_PROFILE})"
        }
        failure {
            echo "nanum-backend 빌드/배포 실패"
        }
    }
}
