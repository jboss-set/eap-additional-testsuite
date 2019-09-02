FROM docker.io/fedora

MAINTAINER Panagiotis Sotiropoulos <psotirop@redhat.com>

LABEL description="A container for running EAT in isolation"

# Upgrading system
RUN dnf -y upgrade
RUN dnf install -y git

# Downloading Java
RUN curl -O https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.1_linux-x64_bin.tar.gz && tar zxvf openjdk-11.0.1_linux-x64_bin.tar.gz && mv jdk-11* /usr/local/

ENV JAVA_HOME /usr/local/jdk-11.0.1

# Add JDK bin dir to PATH
ENV PATH=$PATH:$JAVA_HOME/bin/
ENV MAVEN_VERSION 3.3.9

RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

ENV PATH=$PATH:/usr/share/maven/bin/

RUN mkdir fasterxml && cd fasterxml && git clone https://github.com/panossot/jackson-databind.git -b simulation master && cd master && mvn clean install -DskipTests && FXML_VERSION="2.9.9.3.1" && export FXML_VERSION=$FXML_VERSION && echo $FXML_VERSION && mkdir eap7 && cd eap7 && git clone https://$GITHUB_USERNAME:$GITHUB_PASSWORD@github.com/panossot/wildfly-private.git -b simulation master && cd master && ls -la && sed -i "s/<version.com.fasterxml.jackson.databind>2.9.9.3-redhat-00001/<version.com.fasterxml.jackson.databind>$FXML_VERSION/g" ./pom.xml && mvn clean install -Dmaven.repository.protocol=http -DskipTests && JBOSS_VERSION="$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')" && export JBOSS_VERSION=$JBOSS_VERSION && JBOSS_FOLDER="$(pwd)/dist/target/jboss-eap-7.2" && export JBOSS_FOLDER=$JBOSS_FOLDER && mkdir EAT && cd EAT && git clone https://github.com/panossot/eap-additional-testsuite.git -b simulation master && cd master && mvn clean install -Deap72x -Dstandalone -Dmaven.repository.protocol=http




