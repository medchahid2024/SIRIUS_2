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
				dir('Backend') {
					echo 'Building Spring Boot backend...'

                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Frontend (JavaScript)') {
			steps {
				dir('Frontend') {
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
