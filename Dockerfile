FROM maven:3.2-jdk-7-onbuild
MAINTAINER Liang Ding <dl88250@gmail.com>

ADD . /solo
WORKDIR /solo
RUN mvn install
WORKDIR /solo/target/solo

EXPOSE 8080
