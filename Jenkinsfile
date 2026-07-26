pipeline {
    agent any

    // Định nghĩa các công cụ cần thiết (Cần cài đặt trong Manage Jenkins -> Global Tool Configuration)
    tools {
        maven 'Maven 3'
        jdk 'JDK 21'
        nodejs 'NodeJS 20' // Dành cho web-app
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // Build commonservice trước vì các service khác có thể phụ thuộc vào nó
        stage('Build Commonservice') {
            steps {
                dir('commonservice') {
                    // Cài đặt vào local maven repository (.m2) để các service khác có thể dùng
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        // Build các backend service song song để tiết kiệm thời gian
        stage('Build Backend Services') {
            parallel {
                stage('apigateway') {
                    steps {
                        dir('apigateway') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('authservice') {
                    steps {
                        dir('authservice') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('coreservice') {
                    steps {
                        dir('coreservice') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('discoveryserver') {
                    steps {
                        dir('discoveryserver') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('fileservice') {
                    steps {
                        dir('fileservice') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('notificationservice') {
                    steps {
                        dir('notificationservice') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('subscriptionservice') {
                    steps {
                        dir('subscriptionservice') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        // Build Frontend (Next.js)
        stage('Build Web App') {
            steps {
                dir('web-app') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        // Build Chatbot Platform (Python)
        stage('Setup Chatbot Platform') {
            steps {
                dir('chatbot-platform') {
                    // Chạy cài đặt thư viện Python (yêu cầu Jenkins có sẵn môi trường Python/pip)
                    sh 'pip install -r requirements.txt'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished!'
            // Có thể thêm bước dọn dẹp workspace hoặc gửi thông báo tại đây
            // cleanWs()
        }
        success {
            echo 'Build Successful!'
        }
        failure {
            echo 'Build Failed!'
        }
    }
}
