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

        stage('Deploy') {
			when {
				expression { return params.DEPLOY == true }
            }
            steps {
				script {
					// Ensure we use the artifact you archived earlier
                    def artifact = "target/bookstore-1.0-SNAPSHOT-shaded.jar"

                    // Use the ssh credential id you added in Jenkins
                    sshagent(['deploy-ssh']) {
						// Execute the deploy script from repo (on Windows agent we can call bash if available)
                        // If agent is Windows and does not have bash, use plink or use Publish Over SSH plugin.
                        bat "bash deploy/deploy.sh ${params.DEPLOY_USER}@${params.DEPLOY_HOST} ${params.DEPLOY_PATH} ${artifact}"
                    }
                }
            }
        }
    }

    post {
		success {
			echo ' Build & Test succeeded!'
        }
        failure {
			echo 'Build failed. Check logs.'
        }
    }
}
