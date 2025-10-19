#!/bin/bash

echo "-=-=-=-= 🚢 배포 이미지를 빌드하고 Docker Hub에 푸시합니다 🚢 -=-=-=-="

DOCKERHUB_ID="seoulotakus"
IMAGE_NAME="takumap-be"

docker login

echo "1️⃣ 최신 이미지 빌드"
docker build --platform linux/amd64 -t $IMAGE_NAME .

echo "2️⃣ Docker Hub용 태그 추가"
docker tag $IMAGE_NAME $DOCKERHUB_ID/$IMAGE_NAME:latest

echo "3️⃣ Docker Hub에 이미지 푸시"
docker push $DOCKERHUB_ID/$IMAGE_NAME:latest

echo "✅ Docker Hub 푸시 완료: $DOCKERHUB_ID/$IMAGE_NAME:latest"