#!/bin/bash
# 获取命令字符串
CMD="$0"
# 获取命令绝对路径
SHELL_PATH=`cd $(dirname "$CMD"); pwd -P`
# 判断脚本是否在publish目录下
if test ${SHELL_PATH##*/} != "publish"; then
  echo "err shell path"
  exit
fi
PROJECT_PATH=${SHELL_PATH%/*}
PARENT_PATH=${SHELL_PATH%/*}
echo "${PARENT_PATH}"
cd ${PARENT_PATH}; git pull ;mvn clean package -U -Dmaven.test.skip=true;cd ${SHELL_PATH}
# -maxdepth 1 只在当前目录中查询，不查找子目录
JAR=$(find ${PROJECT_PATH}/target -maxdepth 1 -name "*.jar")
JAR_ORIGINAL=$(find ${PROJECT_PATH}/target -name "*.jar.original")
echo "${JAR}"
if [ -z "${JAR}" ] || [ ! -e "${JAR}" ]; then
  echo "maven package fail !!!"
  exit 1;
fi
unzip -o ${JAR} -d ${PROJECT_PATH}/target/
PROJECT_NAME=${JAR##*/}
PROJECT_NAME=${PROJECT_NAME%.jar}
PACKAGE_PATH=${SHELL_PATH}/${PROJECT_NAME}
#去掉打包后追加的版本号
#PACKAGE_PATH=${PACKAGE_PATH%-*}
if test -d ${PACKAGE_PATH}
  then
    rm -rf ${PACKAGE_PATH}
fi
if test -d ${PACKAGE_PATH}/bin
  then
    rm -f ${PACKAGE_PATH}/bin/*
  else
    mkdir -p ${PACKAGE_PATH}/bin
fi
if test -d ${PACKAGE_PATH}/lib
  then
    rm -f ${PACKAGE_PATH}/lib/*
  else
    mkdir -p ${PACKAGE_PATH}/lib
fi
if test -d ${PACKAGE_PATH}/log
  then
    rm -f ${PACKAGE_PATH}/log/*
  else
    mkdir -p ${PACKAGE_PATH}/log
fi
if test -d ${PACKAGE_PATH}/config
  then
    rm -rf ${PACKAGE_PATH}/config/*
  else
    mkdir -p ${PACKAGE_PATH}/config
fi
# 拷贝resources 下的配置文件到config目录
cp -rf ${PROJECT_PATH}/src/main/resources/* ${PACKAGE_PATH}/config/
cp ${PROJECT_PATH}/target/BOOT-INF/lib/*.jar ${PACKAGE_PATH}/lib/
cp ${JAR_ORIGINAL} ${PACKAGE_PATH}/lib/${PROJECT_NAME}.jar
cp -a ${PROJECT_PATH}/publish/bin/startup.sh ${PACKAGE_PATH}/bin/
echo "========= >>>>>>>>> 打包完成..."