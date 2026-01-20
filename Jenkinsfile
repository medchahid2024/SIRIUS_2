pipeline {
  agent any

  environment {
    BACKEND_DIR = "Backend"
    FRONTEND_DIR = "Frontend"
    BACKEND_JAR = "Backend-1.0-SNAPSHOT.jar"

    DEPLOY_USER = "server"
    DEPLOY_HOST = "172.31.253.154"

    BACKEND_DEPLOY_PATH = "/home/server/back"
    FRONTEND_DEPLOY_PATH = "/home/server/front"

    SSH_PASSWORD = "server"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build Backend') {
      steps {
        dir("${BACKEND_DIR}") {
          sh "mvn clean package -DskipTests"
        }
      }
    }

    stage('Build Frontend') {
      steps {
        dir("${FRONTEND_DIR}") {
          sh """
            npm install
            npm run build
          """
        }
      }
    }

    stage('Deploy (replace)') {
      steps {
        sh """
          echo "=== BACKEND: overwrite jar ==="
          sshpass -p ${SSH_PASSWORD} ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} "mkdir -p ${BACKEND_DEPLOY_PATH}"
          sshpass -p ${SSH_PASSWORD} scp -o StrictHostKeyChecking=no ${BACKEND_DIR}/target/${BACKEND_JAR} ${DEPLOY_USER}@${DEPLOY_HOST}:${BACKEND_DEPLOY_PATH}/${BACKEND_JAR}

          echo "=== FRONTEND: mirror build (delete old files) ==="
          sshpass -p ${SSH_PASSWORD} ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} "mkdir -p ${FRONTEND_DEPLOY_PATH}"
          sshpass -p ${SSH_PASSWORD} rsync -av --delete -e "ssh -o StrictHostKeyChecking=no" ${FRONTEND_DIR}/build/ ${DEPLOY_USER}@${DEPLOY_HOST}:${FRONTEND_DEPLOY_PATH}/
        """
      }
    }
  }
}
