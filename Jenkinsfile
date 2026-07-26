pipeline {
    agent any

    tools {
        maven 'maven'
        // jdk 'jdk21'
        nodejs 'node20'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // Build commonservice trước vì các service khác phụ thuộc vào nó
        stage('Build Commonservice') {
            steps {
                dir('commonservice') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        // Build các backend service song song
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
       
                    sh 'pip install -r requirements.txt'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished!'
            // cleanWs() // Bỏ comment dòng này nếu muốn tự động xóa file sau khi build xong cho nhẹ máy
        }
        success {
            echo 'Build Successful!'
        }
        failure {
            echo 'Build Failed! Vui lòng kiểm tra lại log.'
        }
    }
}
