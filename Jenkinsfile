pipeline {
	agent any

    tools {
		jdk 'jdk21'
        maven 'maven-3.9'
    }

    stages {
		stage('Build') {
			steps {
				bat 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
			steps {
				bat 'mvn test'
            }
            post {
				always {
					junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Debug Artifacts') {
			steps {
				bat 'dir target'
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
			echo '✅ Build & Test succeeded!'
        }
        failure {
			echo '❌ Build failed. Check logs.'
        }
    }
}
