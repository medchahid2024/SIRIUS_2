pipeline {
	agent any

    stages {

		stage('Clone') {
			steps {
				checkout scm
            }
        }

        stage('Build Backend (Spring Boot)') {

			steps {
				dir('proto-back') {
					echo 'Building Spring Boot backend...'

                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Frontend (JavaScript)') {
			steps {
				dir('proto-front') {
					echo 'Building frontend JavaScript...'
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Finish') {
			steps {

				echo 'Build finished!'
            }
        }
    }
}
