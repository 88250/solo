#!/bin/bash

#
# Solo docker 更新重启脚本
#
# 1. 请注意修改参数
# 2. 可将该脚本加入 crontab，每日凌晨运行来实现自动更新
#

restart_solo(){
	docker stop solo
	docker rm solo
	docker run --detach --name solo --network=host \
	--env RUNTIME_DB="MYSQL" \
	--env JDBC_USERNAME="root" \
	--env JDBC_PASSWORD="123456" \
	--env JDBC_DRIVER="com.mysql.cj.jdbc.Driver" \
	--env JDBC_URL="jdbc:mysql://127.0.0.1:3306/solo?useUnicode=yes&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC" \
       	b3log/solo --listen_port=8080 --server_scheme=http --server_host=localhost
}

update_solo(){
  echo "Pulling Solo's image"
	isUpdate=$(docker pull b3log/solo | grep "Downloaded")
	if [[ -z $isUpdate ]]
	then
		echo "Solo is up to date"
	else
		restart_solo >> /dev/null 2>&1
		echo "Restarted Solo"
	fi
}

# 检查当前容器状态，如果状态正常进行升级操作，否则重新进行部署
update_and_test_service(){
	isCrash=$(docker ps | grep "b3log/solo")
	if [[ -z $isCrash ]]
	then
		echo "Solo's status is unexpected, trying to restart it"
		docker pull b3log/solo
		restart_solo
		sleep 5
		isSecondCrash=$(docker ps | grep "b3log/solo")
		if [[ -z $isSecondCrash ]]
		then
			echo "Failed to restart Solo, please check logs via 'docker logs solo'"
		fi
	else
		update_solo
	fi
}

update_and_test_service
