FROM maven:3.8.5-openjdk-17 as builder
#FROM alpine:latest as builder
#RUN apk add --no-cache binutils

#ADD https://download.java.net/java/early_access/alpine/15/binaries/openjdk-14-ea+15_linux-x64-musl_bin.tar.gz /opt/jdk/
#RUN tar -xzvf /opt/jdk/openjdk-14-ea+15_linux-x64-musl_bin.tar.gz -C /opt/jdk/



COPY src /home/app/src
COPY pom.xml /home/app
COPY lombok.config /home/app
RUN mvn -f /home/app/pom.xml clean package && \
    ls -la /home/app/target
RUN MODULES="$(jdeps --list-deps --ignore-missing-deps /home/app/target/java-pure*jar-with-dependencies.jar | tail -n +2 | tr -d ' ' | tr '\n' ',')" && \
    echo "MODULES=$MODULES" && \
    jlink \
    --module-path ${JAVA_HOME}/jmods \
    --add-modules ${MODULES} \
    --verbose \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /opt/jre-minimal



FROM bitnami/minideb:bullseye
#FROM my-java-base:latest
#FROM cgr.dev/chainguard/glibc-dynamic:latest
#FROM cgr.dev/chainguard/gcc-glibc:latest
#FROM cgr.dev/chainguard/gcc-musl:latest




#RUN apk update && apk upgrade && apk add --no-cache java-cacerts bash
COPY --from=builder /opt/jre-minimal /opt/jre-minimal

ENV JAVA_HOME=/opt/jre-minimal
ENV PATH="$PATH:$JAVA_HOME/bin"

# Copy the JRE created in the last step into our $JAVA_HOME
COPY --from=builder  /home/app/target/java-pure*jar-with-dependencies.jar /app.jar

ENTRYPOINT ["/opt/jre-minimal/bin/java","-jar","/app.jar"]
#ENTRYPOINT ["java","--version"]