#!/bin/bash

## author: tomaer.Ma <i@tomaer.com>

DATEBASE_TYPE=${DATEBASE_TYPE:-h2}

if [ ! -f "/opt/b3log/solo/WEB-INF/classes/local.properties" ]; then
    if [ "$DATABASE_TYPE" == "mysql" ]; then
        cat /opt/b3log/tmp/local.properties.mysql | sed \
         -e "s|{{DATABASE_HOST}}|${DATABASE_HOST}|g" \
         -e "s|{{DATABASE_PORT}}|${DATABASE_PORT:-3306}|g" \
         -e "s|{{DATABASE_NAME}}|${DATABASE_NAME:-solo}|g" \
         -e "s|{{DATABASE_USERNAME}}|${DATABASE_USERNAME:-root}|g" \
         -e "s|{{DATABASE_PASSWORD}}|${DATABASE_PASSWORD}|g" \
         > /opt/b3log/solo/WEB-INF/classes/local.properties
    else
        cp /opt/b3log/tmp/local.properties.h2 /opt/b3log/solo/WEB-INF/classes/local.properties
    fi

    cat /opt/b3log/tmp/latke.properties | sed \
     -e "s|{{SERVER_SCHMEA}}|${SERVER_SCHMEA:-http}|g" \
     -e "s|{{SERVER_NAME}}|${SERVER_NAME:-localhost}|g" \
    > /opt/b3log/solo/WEB-INF/classes/latke.properties
	
    cat /opt/b3log/tmp/mail.properties | sed \
     -e "s|{{EMAIL_ADDRESS}}|${EMAIL_ADDRESS}|g" \
     -e "s|{{EMAIL_PASSWORD}}|${EMAIL_PASSWORD}|g" \
     -e "s|{{SMTP_HOST}}|${SMTP_HOST:-smtp.gmail.com}|g" \
     -e "s|{{SMTP_PROT}}|${SMTP_PROT:-587}|g" \
     -e "s|{{SMTP_SOCKETFACTORY_PORT}}|${SMTP_SOCKETFACTORY_PORT:-465}|g" \
    > /opt/b3log/solo/WEB-INF/classes/mail.properties
    rm -rf /opt/b3log/tmp
fi

java -cp WEB-INF/lib/*:WEB-INF/classes org.b3log.solo.Starter
