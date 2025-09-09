pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven-3.9'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Himanshu14x/ConsoleBookstore.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*-shaded.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Build & Test succeeded!'
        }
        failure {
            echo 'Build failed. Check logs.'
        }
    }
}
