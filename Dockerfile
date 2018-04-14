FROM gcr.io/google-appengine/openjdk:8

 COPY target/locatemeapp-1.0.jar locatemeapp-1.0.jar
 CMD [ "java", "-jar","locatemeapp-1.0.jar", "server"]