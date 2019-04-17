FROM docker.io/eateap

MAINTAINER Panagiotis Sotiropoulos <psotirop@redhat.com>

RUN chmod -R 777 /home/user/.m2 && chmod -R 777 /eap/dist && chmod -R 777 /eat/master

# Add JDK bin dir to PATH
ENV PATH=$PATH:/usr/java/latest/bin/

ENV PATH=$PATH:/usr/share/maven/bin/

WORKDIR "/eat/master"

ENV JBOSS_VERSION=$JBOSS_VERSION
ENV JBOSS_FOLDER=$JBOSS_FOLDER
ENV SERVER_CODE=$SERVER_CODE

CMD mvn clean install -D$SERVER_CODE -Dstandalone -s /home/user/.m2/settings.xml




