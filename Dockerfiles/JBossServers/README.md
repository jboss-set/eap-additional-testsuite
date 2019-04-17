# RUN EAT with OPENSHIFT
--------------------------


1. Use Dockerfile in DockerfileLayer1 dir to create image docker.io/eateap : docker build -t docker.io/eateap .
2. Add in the container of this image the eap dist and the maven repo artifacts that are needed for EAT 
3. Commit the changes
4. Use Dockerfile in DockerfileLayer2 dir to create image docker.io/eat:eap7x : docker build -t docker.io/eat:eap7x .
5. Push the tagged image to some repo : docker push docker.io/jboss/eat:eap7x
6. In Openshift create a project : e.g. eat
7. Get started with your project using 'Deploy Image'. e.g. choose image name and refer to the image uploaded at the repo in the previous step
8. Set the environment variables JBOSS_VERSION='the version of the server' , JBOSS_FOLDER=/eap/dist/target/jboss-eap-'the version of the server' and SERVER_CODE='code of the server used with eat, eg eap72x'
9. Push the Deploy button and check EAT EAP running in Openshift (e.g. using the Logs)

