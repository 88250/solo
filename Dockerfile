FROM anapsix/alpine-java:8_jdk
LABEL maintainer="Tomaer Ma<i@tomaer.com>"

WORKDIR /opt/b3log/solo
ADD ./target/solo/ $WORKDIR
ADD ./src/main/resources/docker/entrypoint.sh $WORKDIR

RUN mkdir -p /opt/b3log/backup/ && mkdir -p /opt/b3log/tmp/ \
    && cp -r /opt/b3log/solo/plugins/ /opt/b3log/backup/ \
    && cp -r /opt/b3log/solo/skins/ /opt/b3log/backup/ \
    && rm -rf WEB-INF/classes/local.properties WEB-INF/classes/mail.properties WEB-INF/classes/latke.properties \
    && chmod 777 /opt/b3log/solo/entrypoint.sh

ADD ./src/main/resources/docker/local.properties.h2 /opt/b3log/tmp
ADD ./src/main/resources/docker/local.properties.mysql /opt/b3log/tmp
ADD ./src/main/resources/docker/mail.properties /opt/b3log/tmp
ADD ./src/main/resources/docker/latke.properties /opt/b3log/tmp

VOLUME ["/opt/b3log/backup/"]

EXPOSE 8080

ENTRYPOINT [ "/opt/b3log/solo/entrypoint.sh" ]
