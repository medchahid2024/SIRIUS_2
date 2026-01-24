#!/bin/bash
set -e

BACKEND_DIR="/home/server/back"
FRONTEND_DIR="/home/server/front"
JAR="Backend-1.0-SNAPSHOT.jar"
FRONT_PORT=3000

echo "Stopping backend..."
pkill -f "java -jar $JAR" || true

echo "Starting backend..."
cd "$BACKEND_DIR"
nohup java -jar "$JAR" --spring.profiles.active=vm > backend.log 2>&1 < /dev/nu>

echo "Stopping frontend (port ${FRONT_PORT})..."
fuser -k -n tcp ${FRONT_PORT} || true

echo "Starting frontend..."
cd "$FRONTEND_DIR"
