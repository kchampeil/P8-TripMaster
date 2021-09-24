FROM openjdk:8-jdk-alpine

COPY build/libs/*.jar tour-guide.jar

# option 1 : active profile set in docker-compose
ENTRYPOINT ["java","-jar","/tour-guide.jar"]

# option 2a : active profile set when setting the entrypoint
#ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/tour-guide.jar"]

# option 2b : active profile set when setting the entrypoint
#ENV SPRING_PROFILES_ACTIVE=docker
#ENTRYPOINT ["java","-jar","/tour-guide.jar"]
