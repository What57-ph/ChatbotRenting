pipeline {
    agent any

    environment {

        VERCEL_TOKEN = credentials('VERCEL_TOKEN')
        VERCEL_ORG_ID = credentials('VERCEL_ORG_ID')
        VERCEL_PROJECT_ID = credentials('VERCEL_PROJECT_ID')
    }

    tools {
        maven 'maven'
        jdk 'jdk21'
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

        // Deploy Frontend (Next.js) lên Vercel
        stage('Deploy Web App to Vercel') {
            steps {
                dir('web-app') {
                    sh 'npm install'
                    sh 'rm -rf .vercel'
                    sh 'npx vercel pull --yes --environment=production --token=$VERCEL_TOKEN'
                    sh 'npx vercel build --prod --token=$VERCEL_TOKEN'
                    sh 'npx vercel deploy --prebuilt --prod --token=$VERCEL_TOKEN'
                }
            }
        }

        // Build Chatbot Platform (Python)
        // stage('Setup Chatbot Platform') {
        //     steps {
        //         dir('chatbot-platform') {
        //             sh 'pip install -r requirements.txt'
        //         }
        //     }
        // }
    }

    post {
        always {
            echo 'Pipeline finished!'
        }
        success {
            echo 'Build & Deploy Successful!'
        }
        failure {
            echo 'Build Failed! Vui lòng kiểm tra lại log trên Jenkins.'
        }
    }
}
