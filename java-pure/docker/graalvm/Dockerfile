#FROM maven:3.8.5-openjdk-17 as builder
#
#COPY src /home/app/src
#COPY pom.xml /home/app
#COPY lombok.config /home/app
#RUN mvn -f /home/app/pom.xml clean package
#



FROM ghcr.io/graalvm/graalvm-ce:ol9-java17
RUN gu install native-image

# Copy the JRE created in the last step into our $JAVA_HOME
#COPY --from=builder  /home/app/target/java-pure*jar-with-dependencies.jar /app.jar


#ENTRYPOINT ["/opt/jre-minimal/bin/java","-jar","/app.jar"]