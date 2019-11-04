FROM maven:3-jdk-8-alpine as MVN_BUILD

WORKDIR /opt/solo/
ADD . /tmp
RUN cd /tmp && mvn package -DskipTests -Pci -q && mkdir target/solo/ && unzip -q target/solo.jar -d target/solo/ && mv target/solo/* /opt/solo/ \
&& cp -f /tmp/src/main/resources/docker/* /opt/solo/

FROM openjdk:8-alpine
LABEL maintainer="Liang Ding<d@b3log.org>"

WORKDIR /opt/solo/
COPY --from=MVN_BUILD /opt/solo/ /opt/solo/
RUN apk add --no-cache ca-certificates tzdata

ENV TZ=Asia/Shanghai
EXPOSE 8080

ENTRYPOINT [ "java", "org.b3log.solo.Server" ]
