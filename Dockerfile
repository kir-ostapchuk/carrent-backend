# This file is not needed in current implementation just run bootBuildImage
# for a concrete microservice to push the image to container registry/

#FROM gradle:7.2.0-jdk17 AS build
#COPY --chown=gradle:gradle . /home/gradle/src
#WORKDIR /home/gradle/src
#RUN gradle clean build bootBuildImage
#
#FROM openjdk:17-jdk-slim
#EXPOSE 8080
#RUN mkdir /app
#COPY --from=build /home/gradle/src/build/libs/*.jar /app/core-service.jar
#CMD ["java", "-jar", "/app/core-service.jar"]