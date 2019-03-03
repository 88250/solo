#!/bin/bash
#
# Solo - A small and beautiful blogging system written in Java.
# Copyright (c) 2010-2019, b3log.org & hacpai.com
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#


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
     -e "s|{{SERVER_PORT}}|${SERVER_PORT:-8080}|g" \
    > /opt/b3log/solo/WEB-INF/classes/latke.properties

    rm -rf /opt/b3log/tmp
fi

java -cp WEB-INF/lib/*:WEB-INF/classes org.b3log.solo.Starter
