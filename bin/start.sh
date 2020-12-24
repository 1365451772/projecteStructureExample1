#!/bin/bash

cd "$(dirname "$0")" || exit
HOME=$(pwd)
LIB_DIR="${HOME}/lib/*"
LOG=${HOME}/log
CONF_DIR="${HOME}/config"
CONSOLE_MAIN=com.bangcle.firmware.spider.manager.StartUpApplication

if [ -n  "$APP_DEBUG" ]
then
  JVM_DBG_OPTS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"
  echo "will start application in debug mode listen port:8000"
elif [ -n "$APP_STARTUP_DEBUG" ]
then
  JVM_DBG_OPTS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y"
  echo "will start application in debug mode and suspend at startup, listen port:8000"
else
  JVM_DBG_OPTS=
fi

# 创建log目录，否则gc不打印
if [ ! -d "${LOG}" ]; then
  mkdir ${LOG}
fi

DEFAULT_JVM_OPTS="-server -Xmx256m -Xms256m -Xmn200m -XX:MetaspaceSize=90m -XX:MaxMetaspaceSize=90m -Xss256k -Xloggc:${LOG}/gc.log -XX:-PrintGCDetails -XX:+PrintHeapAtGC -XX:+PrintGCDateStamps -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCApplicationStoppedTime -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=64m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"
# 优先设置环境变量里设置的JVM配置
if [[ -n "${JVM_OPTS}" ]] ; then
DEFAULT_JVM_OPTS=${JVM_OPTS}
fi

echo "start aplication from main class ${CONSOLE_MAIN}..."
java ${DEFAULT_JVM_OPTS} ${JVM_DBG_OPTS} -classpath ${LIB_DIR}:${CONF_DIR}:. -DLOG_PATH=log ${CONSOLE_MAIN} "$@"
