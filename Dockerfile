FROM maven:3
MAINTAINER Liang Ding <d@b3log.org>

ADD . /solo
WORKDIR /solo
RUN mvn install
WORKDIR /solo/target/solo

EXPOSE 8080

CMD ["/bin/sh", "-c", "java -cp WEB-INF/lib/*:WEB-INF/classes org.b3log.solo.Starter"]