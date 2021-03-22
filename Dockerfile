FROM maven:3-jdk-8-alpine as MVN_BUILD

WORKDIR /opt/solo/
ADD . /tmp
RUN cd /tmp && mvn package -DskipTests -Pci -q && mv target/solo/* /opt/solo/ \
&& cp -f /tmp/src/main/resources/docker/* /opt/solo/

FROM openjdk:8-alpine
LABEL maintainer="Liang Ding<845765@qq.com>"

WORKDIR /opt/solo/
COPY --from=MVN_BUILD /opt/solo/ /opt/solo/
RUN apk add --no-cache ca-certificates tzdata

ENV TZ=Asia/Shanghai
ARG git_commit=0
ENV git_commit=$git_commit

EXPOSE 8080

ENTRYPOINT [ "java", "-cp", "lib/*:.", "org.b3log.solo.Server" ]
