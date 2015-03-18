#
# Docker Visualization Dockerfile
#
FROM debian
MAINTAINER Shane Witbeck <shane@digitalsanctum.com>

RUN apt-get update && apt-get install -y curl

# install Java
ENV JDK_VERSION 8
ENV JDK_UPDATE_VERSION 40
ENV JDK_BUILD_VERSION b26
RUN curl -LO "http://download.oracle.com/otn-pub/java/jdk/${JDK_VERSION}u${JDK_UPDATE_VERSION}-${JDK_BUILD_VERSION}/jdk-${JDK_VERSION}u${JDK_UPDATE_VERSION}-linux-x64.tar.gz" -H 'Cookie: oraclelicense=accept-securebackup-cookie'
RUN tar -xzf jdk-${JDK_VERSION}u${JDK_UPDATE_VERSION}-linux-x64.tar.gz && rm *.tar.gz
ENV JAVA_HOME=/jdk1.${JDK_VERSION}.0_${JDK_UPDATE_VERSION}
ENV PATH=$JAVA_HOME/bin:$PATH

ADD target/docker-viz .

EXPOSE 4567

CMD docker-viz