FROM docker.io/fedora

MAINTAINER Panagiotis Sotiropoulos <psotirop@redhat.com>

LABEL description="A container for running EAT in isolation"

# Upgrading system
RUN dnf -y upgrade
RUN dnf -y install wget
RUN dnf install -y git

# Downloading Java
RUN wget --no-check-certificate "https://www.dropbox.com/s/vm819mraxwidxqh/jdk-8u191-linux-x64.rpm?dl=0" -O /tmp/jdk-8-linux-x64.rpm && cd /tmp && dnf -y --allowerasing install findutils && rpm -Uhv jdk-8-linux-x64.rpm

RUN rm /tmp/jdk-8-linux-x64.rpm 

RUN alternatives --install /usr/bin/java jar /usr/java/latest/bin/java 200000
RUN alternatives --install /usr/bin/javaws javaws /usr/java/latest/bin/javaws 200000
RUN alternatives --install /usr/bin/javac javac /usr/java/latest/bin/javac 200000

ENV JAVA_HOME /usr/java/latest

# Add JDK bin dir to PATH
ENV PATH=$PATH:/usr/java/latest/bin/

ENV MAVEN_VERSION 3.5.3

RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

ENV PATH=$PATH:/usr/share/maven/bin/

ENV GLIBC_VERSION=2.27-r0


RUN dnf -y install gcc && dnf -y install zlib-devel && dnf -y install glibc-devel.i686 glibc.i686

ENV GRAAL_VERSION 19.0.2
ENV GRAAL_HOME /usr/lib/jvm/vm-$GRAAL_VERSION
ENV PATH $GRAAL_HOME/graalvm-ce-$GRAAL_VERSION/bin:$PATH

RUN mkdir -p $GRAAL_HOME &&  wget --no-check-certificate https://www.dropbox.com/s/n0q7ek0xbma6nel/graal.tar?dl=0 -O $GRAAL_HOME/graalvm-ce-$GRAAL_VERSION-linux-amd64.tar 


RUN tar xvf $GRAAL_HOME/graalvm-ce-$GRAAL_VERSION-linux-amd64.tar -C $GRAAL_HOME \
     && rm "$GRAAL_HOME/graalvm-ce-$GRAAL_VERSION-linux-amd64.tar"

ENV GRAALVM_HOME $GRAAL_HOME/graalvm-ce-$GRAAL_VERSION

RUN mkdir protean && cd protean && git clone https://github.com/quarkusio/quarkus.git master && cd master && ls -la && mvn clean install -DskipTests --fail-never && export QUARKUS_VERSION=999-SNAPSHOT &&  JBOSS_VERSION=999.0.0-SNAPSHOT && export JBOSS_VERSION=$JBOSS_VERSION && mkdir EAT && cd EAT && git clone https://github.com/jboss-set/eap-additional-testsuite.git && cd eap-additional-testsuite && export NATIVE=-Dnative && ./run-quarkus.sh




