#!/bin/bash

cd `dirname $0`
cd ..
HOME=`pwd`
LIB_DIR=${HOME}/lib/*
LOG=${HOME}/log
CONSOLE_MAIN=com.bangcle.firmware.spider.manager.StartUpApplication
CONF_DIR="${HOME}/config"
PID_NAME=firmware-spider-manager

JVM_OPTS="-server -Xmx256m -Xms256m -Xmn96m -XX:MetaspaceSize=90m -XX:MaxMetaspaceSize=90m -Xss256k -Xloggc:${LOG}/gc.log -XX:-PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCApplicationStoppedTime -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=64m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"

start() {
  java ${JVM_OPTS} -classpath ${LIB_DIR}:${CONF_DIR}:. -DLOG_PATH=log ${CONSOLE_MAIN} > /dev/null 2>&1 &
  pid=`ps aux | grep java | grep ${PID_NAME} | grep -v 'grep' | awk '{print $2}'`
  echo "=====>>>> 进程ID:${pid},启动成功，请查看日志"
}

debug() {
  java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=10003 ${JVM_OPTS} -classpath ${LIB_DIR}:${CONF_DIR}:. -DLOG_PATH=log ${CONSOLE_MAIN} > /dev/null 2>&1 &
  pid=`ps aux | grep java | grep ${PID_NAME} | grep -v 'grep' | awk '{print $2}'`
  echo "=====>>>> 进程ID:${pid},启动成功，请查看日志"
}

stop() {
    # ps -u $USER |awk '$NF ~ /java/ {print $1}'|xargs kill
    sync
    for i in `ps aux | grep java | grep ${PID_NAME} | grep -v 'grep' | awk '{print $2}'`
    do
      echo "run pid:"${i}
      kill -15 ${i}
	    echo "pid:"${i}",killed success..."
    done
    sleep 3
}

restart() {
    stop
    sleep 3
    start
}

case "$1" in
    start)
        start
        ;;
    debug)
        debug
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    *)
        echo "Usage: { start |debug |stop | restart | }"
        exit 1
esac





