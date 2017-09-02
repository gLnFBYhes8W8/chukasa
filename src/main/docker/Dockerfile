FROM gcr.io/google_appengine/openjdk8
VOLUME /tmp
ADD chukasa-0.0.1-SNAPSHOT.jar app.jar
CMD [ "java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dspring.profiles.active=gae-flexible-postgresql","/app.jar"]