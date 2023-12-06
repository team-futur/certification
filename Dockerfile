# OpenJDK 17 베이스 이미지 사용
FROM openjdk:17

# 애플리케이션 파일을 컨테이너 내부로 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 환경 변수로 프로파일 설정
ENV SPRING_PROFILES_ACTIVE=dev

# 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "/app.jar"]

# 1. 도커 빌드하기
#   docker build --platform linux/amd64 -t futur-certification-image .
# 2. 이미지 저장하기
#   docker save -o futur-certification-image.tar futur-certification-image
# 3. 서버에서 이미지 불러오기
#   docker load -i futur-certification-image.tar
# 4. 실행(-P : 랜덤포트 / -d : 백그라운드)
#   docker run -it --add-host host.docker.internal:host-gateway -d -p 2030:2030 --network futur-network --name futur-certification-container futur-certification-image
# 5. 고유 포트(외부포트:내부포트)
#   docker run -p 8080:8080 futur-certification-image
# 6. 로그 확인
#   docker logs -f futur-certification-container