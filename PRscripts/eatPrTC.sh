#!/bin/bash

set -e

spr=%ServerPrNum%
echo %teamcity.build.branch%

cd server
export JBOSS_VERSION="$(./../Maven/apache-maven-3.3.9/bin/mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')"
cd ..

if [[ $spr != "null" ]]; then
    mkdir -p server2
    cd server2
    origin=https://github.com/wildfly/wildfly.git
    git clone $origin
    cd wildfly
  #  IFS=',' read -r -a array <<< "$spr";

  #  for prn in "${array[@]}"
  #  do
        git fetch -f $origin +refs/pull/$spr/merge
     #   git merge --no-ff -m "merging" FETCH_HEAD
        git checkout FETCH_HEAD
  #  done
    
    ./../../Maven/apache-maven-3.5.3/bin/mvn clean install -DskipTests
    cd ../..

    export JBOSS_FOLDER=$PWD/server2/wildfly/dist/target/wildfly-$JBOSS_VERSION
    export JBOSS_GIT_DIR=$PWD/server2/wildfly
else
    export JBOSS_FOLDER=$PWD/server/dist/target/wildfly-$JBOSS_VERSION
    export JBOSS_GIT_DIR=$PWD/server
fi

echo $JBOSS_FOLDER
echo $JBOSS_VERSION
echo $JBOSS_GIT_DIR
echo %EatPrNums%

cd eat
cd eap-additional-testsuite
git log -n 10
./Maven/apache-maven-3.3.9/bin/mvn clean install -Dwildfly -Dstandalone -DdSources -Djdk8
