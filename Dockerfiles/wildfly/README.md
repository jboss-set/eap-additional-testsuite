# RUN EAT with OPENSHIFT
--------------------------

In order for run EAT Wildfly with Openshift, please, follow the next steps ...

1. Create the image for EAT Wildfly : e.g. docker build -t docker.io/eat .
2. Tag the previous image : e.g. docker tag docker.io/eat:latest docker.io/jboss/eat:wildfly1700B1
3. Push the tagged image to some repo : docker push docker.io/jboss/eat:wildfly1700B1
4. In Openshift create a project : e.g. eat
5. Get started with your project using 'Deploy Image'. e.g. choose image name and refer to the image uploaded at the repo in the previous step
6. Set the environment variables JBOSS_VERSION='the version of the server' and JBOSS_FOLDER=/wildfly/master/dist/target/wildfly-'the version of the server' 
7. Push the Deploy button and check EAT Wildfly running in Openshift (e.g. using the Logs)

