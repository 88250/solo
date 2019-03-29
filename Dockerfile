FROM maven:3-jdk-8-alpine
LABEL maintainer="Liang Ding<d@b3log.org>"

WORKDIR /opt/solo
ADD . /tmp

RUN apk add tzdata \
    && cd /tmp && mvn package -DskipTests -Pci && mv target/solo/* /opt/solo/ \
    && cp -f /tmp/src/main/resources/docker/* /opt/solo/WEB-INF/classes/ \
    && rm -rf /tmp/* && rm -rf ~/.m2

EXPOSE 8080

ENTRYPOINT [ "java", "-cp", "WEB-INF/lib/*:WEB-INF/classes", "org.b3log.solo.Starter" ]
