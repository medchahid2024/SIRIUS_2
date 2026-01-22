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
  stage('Run (restart services)') {
    steps {
      sh """
        echo "=== BACKEND: stopper old + start new ==="
        sshpass -p ${SSH_PASSWORD} ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
          set -e
          cd ${BACKEND_DEPLOY_PATH}

          # Stop  backend en cours
          pkill -f "java -jar ${BACKEND_JAR}" || true

          # Start backend in background
          nohup java -jar ${BACKEND_JAR} --spring.profiles.active=vm > backend.log 2>&1 &
        '

        echo "=== FRONTEND: stop old + start new ==="
        sshpass -p ${SSH_PASSWORD} ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
          set -e
          cd ${FRONTEND_DEPLOY_PATH}

          # Stop  frontend en cours
          pkill -f "http-server .* -p 3000" || true
          pkill -f "http-server.*3000" || true

          # Start static server in background
          nohup http-server . -p 3000 > frontend.log 2>&1 &
        '
      """
    }
  }



  }
}
