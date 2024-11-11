# 基础镜像
FROM openjdk:8-jre-slim

# 作者
MAINTAINER rem

# 配置
ENV PARAMS=""

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 添加应用
ADD rem-mall-web.jar /rem-mall-web.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /rem-mall-web.jar $PARAMS"]