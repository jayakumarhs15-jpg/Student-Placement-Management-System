pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven'
    }

    stages {
        stage('Verify Project') {
            steps {
                bat 'if not exist "backend\\mvnw.cmd" exit /b 1'
                echo 'Project files verified.'
            }
        }

        stage('Build and Test') {
            steps {
                withCredentials([string(credentialsId: 'mysql-db-password', variable: 'DB_PASSWORD')]) {
                    dir('backend') {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('Copy Test Reports') {
            steps {
                bat 'if not exist "%WORKSPACE%\\test-reports" mkdir "%WORKSPACE%\\test-reports"'
                bat 'copy /Y "backend\\target\\surefire-reports\\*.xml" "%WORKSPACE%\\test-reports\\"'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit testResults: 'test-reports/*.xml', allowEmptyResults: false
            }
        }
    }

    post {
        success {
            echo 'Build and all tests passed successfully!'
        }
        failure {
            echo 'Build failed. Check the Console Output.'
        }
        always {
            echo 'Jenkins pipeline execution completed.'
        }
    }
}