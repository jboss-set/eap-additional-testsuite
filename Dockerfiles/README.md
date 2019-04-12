# EAP ADDITIONAL TESTSUITE
--------------------------
## RUNNING EAT USING DOCKER IN ORDER TO AVOID PORT CONFLICTS
------------------------------------------------------------


### JBOSS SERVERS
-----------------
Using the Dockerfile in the JBossServers directory you need to mount the server dist directory as well as the maven local repository directory.

1. export JBOSS_FOLDER="the dist directory of the server you would like to test"
2. export JBOSS_VERSION="the version of the server to test"
3. export SERVER_CODE=eap71x, (wildfly, eap7, eap70x, eap64x, etc)
4. export MAVEN_REPO="the path to the .m2 maven directory" (the file settings.xml, inside this directory, should have the line ```<localRepository>/...path.../.m2/repository</localRepository>```)
5. chcon -Rt svirt_sandbox_file_t $JBOSS_FOLDER (every time we set another SERVER_CODE we should give the SELinux permissions for the JBOSS_FOLDER)
6. chcon -Rt svirt_sandbox_file_t $MAVEN_REPO
7. Go to dir Dockerfiles/JBossServers
8. Run : docker build -t docker.io/eat . (in order too create the eat image)
9. Run : docker run -t --name=eat -e JBOSS_FOLDER=$JBOSS_FOLDER -e JBOSS_VERSION=$JBOSS_VERSION -e MAVEN_REPO=$MAVEN_REPO -e SERVER_CODE=$SERVER_CODE -v $JBOSS_FOLDER:$JBOSS_FOLDER -v $MAVEN_REPO:$MAVEN_REPO  docker.io/eat (in order to execute eat)


### WILDFLY
-----------
Using the Dockerfile in the wildfly directory you do not need to mount the server dir as it is downloaded as part of the image.

1. Go to dir Dockerfiles/wildfly
2. Run : docker build -t docker.io/eat . (in order too create the eat image)
3. Run : docker run -t --name=eat -e JBOSS_FOLDER=/wildfly/master/dist/target/wildfly-13.0.0.Beta2-SNAPSHOT -e JBOSS_VERSION=13.0.0.Beta2-SNAPSHOT  docker.io/eat (in order to execute eat, the version should be replaced with the current wildfly master version)

