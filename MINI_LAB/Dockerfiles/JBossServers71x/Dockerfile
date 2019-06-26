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

ENV MAVEN_VERSION 3.3.9

RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

ENV PATH=$PATH:/usr/share/maven/bin/

RUN mkdir eap7 && cd eap7 && git clone https://$GITHUB_USERNAME:$GITHUB_PASSWORD@github.com/jbossas/jboss-eap7.git --branch 7.1.x && cd jboss-eap7 && ls -la && mvn clean install -Dmaven.repository.protocol=http && JBOSS_VERSION="$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')" && export JBOSS_VERSION=$JBOSS_VERSION && JBOSS_FOLDER="$(pwd)/dist/target/jboss-eap-7.1" && export JBOSS_FOLDER=$JBOSS_FOLDER && mkdir EAT && cd EAT && git clone https://github.com/jboss-set/eap-additional-testsuite.git master && cd master && mvn clean install -Deap71x -Dstandalone -Dmaven.repository.protocol=http




