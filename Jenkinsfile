pipeline {
  agent any

  environment {
    BACKEND_DIR  = "Backend"
    FRONTEND_DIR = "Frontend"
    BACKEND_JAR  = "Backend-1.0-SNAPSHOT.jar"

    DEPLOY_USER = "server"
    DEPLOY_HOST = "172.31.252.250"

    BACKEND_DEPLOY_PATH  = "/home/server/back"
    FRONTEND_DEPLOY_PATH = "/home/server/front"

    SSH_PASSWORD = "server"
    RUN_SCRIPT = "/home/server/run.sh"
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
          echo "=== Create remote folders ==="
          sshpass -p '${SSH_PASSWORD}' ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} "
            mkdir -p ${BACKEND_DEPLOY_PATH}
            mkdir -p ${FRONTEND_DEPLOY_PATH}
          "

          echo "=== Upload backend jar ==="
          sshpass -p '${SSH_PASSWORD}' scp -o StrictHostKeyChecking=no ${BACKEND_DIR}/target/${BACKEND_JAR} ${DEPLOY_USER}@${DEPLOY_HOST}:${BACKEND_DEPLOY_PATH}/${BACKEND_JAR}

          echo "=== Upload frontend build ==="
          sshpass -p '${SSH_PASSWORD}' rsync -av --delete -e "ssh -o StrictHostKeyChecking=no" ${FRONTEND_DIR}/build/ ${DEPLOY_USER}@${DEPLOY_HOST}:${FRONTEND_DEPLOY_PATH}/
        """
      }
    }

    stage('Run on VM') {
      steps {
        sh """
          echo "=== Run  script ==="
          sshpass -p '${SSH_PASSWORD}' ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} "
            chmod +x ${RUN_SCRIPT}
            ${RUN_SCRIPT}
          "
        """
      }
    }
  }
}