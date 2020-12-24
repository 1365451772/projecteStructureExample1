FROM 192.168.138.182:8029/library/ubuntu16-oraclejdk8:2.1

LABEL author=ZengQingBin
LABEL name=firmware-spider-manager

WORKDIR /data/app
ARG DEPEND_LIB
ADD ${DEPEND_LIB} /data/app/lib/
ARG JAR_FILE
ADD ${JAR_FILE} /data/app/lib/firmware-spider-manager.jar
ARG CONF_FILE
ADD ${CONF_FILE} /data/app/config/
ARG LOGBACK
ADD ${LOGBACK} /data/app/config/

ADD ./bin/*.sh /data/app/

RUN chmod -R 777 /data/app

ENTRYPOINT ["/data/app/entrypoint.sh"]
EXPOSE 8066

