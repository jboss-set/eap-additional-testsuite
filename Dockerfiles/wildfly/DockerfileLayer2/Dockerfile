FROM docker.io/eatwildfly

MAINTAINER Panagiotis Sotiropoulos <psotirop@redhat.com>

RUN chmod -R 777 /home/user/.m2 && chmod -R 777 /wildfly/dist && chmod -R 777 /eat/master

# Add JDK bin dir to PATH
ENV PATH=$PATH:/usr/java/latest/bin/

ENV PATH=$PATH:/usr/share/maven/bin/

WORKDIR "/eat/master"

ENV JBOSS_VERSION=$JBOSS_VERSION
ENV JBOSS_FOLDER=$JBOSS_FOLDER

CMD mvn clean install -Dwildfly -Dstandalone -s /home/user/.m2/settings.xml




