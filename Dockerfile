FROM maven:3-jdk-8-alpine
LABEL maintainer="Tomaer Ma<i@tomaer.com>"

ADD . /tmp

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone \
    && cd /tmp && mvn package -DskipTests -Pci && mv target/solo/* /opt/solo/ \
    && cp -f /tmp/src/main/resources/docker/* /opt/solo/WEB-INF/classes/ \
    && rm -rf /tmp/* && rm -rf ~/.m2

EXPOSE 8080

WORKDIR /opt/solo
ENTRYPOINT [ "java", "-cp", "WEB-INF/lib/*:WEB-INF/classes", "org.b3log.solo.Starter" ]
