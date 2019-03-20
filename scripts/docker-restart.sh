#!/bin/bash

#
# Solo docker 更新重启脚本
#
# 1. 请注意修改参数
# 2. 可将该脚本加入 crontab，每日凌晨运行来实现自动更新
#

docker pull b3log/solo
docker stop solo
docker rm solo
docker run --detach --name solo --network=host \
    --env RUNTIME_DB="MYSQL" \
    --env JDBC_USERNAME="root" \
    --env JDBC_PASSWORD="123456" \
    --env JDBC_DRIVER="com.mysql.cj.jdbc.Driver" \
    --env JDBC_URL="jdbc:mysql://127.0.0.1:3306/solo?useUnicode=yes&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC" \
    b3log/solo --listen_port=8080 --server_scheme=http --server_host=localhost
