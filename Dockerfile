FROM openjdk:22-jdk
ADD target/user-login.jar user-login.jar
ENTRYPOINT ["java", "-jar", "/user-login.jar"]
