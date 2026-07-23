#!/usr/bin/env bash
# MusicLV 배포 스크립트 — GitHub Actions(SSH)가 호출하거나 수동 실행
#   git pull → gradlew bootJar → 서비스 재시작 → 헬스체크
# 실패하면 이전 jar 로 되돌린다.
set -euo pipefail
exec > >(tee -a /opt/musiclv/deploy.log) 2>&1

APP_DIR=/opt/musiclv
JAR=$APP_DIR/musiclv.jar
PREV=$APP_DIR/musiclv.jar.prev
PORT=8081

echo "=================================================="
echo "[deploy] START $(date '+%F %T %Z')  from=${SSH_CLIENT:-local}"
cd "$APP_DIR"

echo "[deploy] git pull..."
git fetch --prune origin
git reset --hard origin/main

echo "[deploy] build..."
chmod +x ./gradlew
./gradlew clean bootJar --no-daemon

NEW_JAR=$(ls -t build/libs/*.jar | grep -v plain | head -1)
if [ -z "$NEW_JAR" ]; then
  echo "[deploy] FAIL: 빌드 결과 jar 를 찾지 못했습니다."
  exit 1
fi

# 롤백용으로 직전 jar 를 남긴다
[ -f "$JAR" ] && cp -f "$JAR" "$PREV"
cp -f "$NEW_JAR" "$JAR"

echo "[deploy] restart service..."
sudo /usr/bin/systemctl restart musiclv

echo "[deploy] health check..."
for i in $(seq 1 60); do
  code=$(curl -s -o /dev/null -w "%{http_code}" "http://127.0.0.1:$PORT/" 2>/dev/null || echo 000)
  if [ "$code" = "200" ]; then
    echo "[deploy] DONE $(date '+%F %T %Z')  commit=$(git rev-parse --short HEAD)  (${i}s)"
    # 빌드 캐시가 디스크를 잠식하지 않도록 정리
    rm -rf build/classes build/tmp
    exit 0
  fi
  sleep 1
done

echo "[deploy] FAIL: 헬스체크 실패. 이전 버전으로 되돌립니다."
if [ -f "$PREV" ]; then
  cp -f "$PREV" "$JAR"
  sudo /usr/bin/systemctl restart musiclv
  echo "[deploy] 롤백 완료."
fi
exit 1
