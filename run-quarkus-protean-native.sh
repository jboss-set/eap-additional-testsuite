#!/bin/bash

sed -i 's/${QUARKUS_VERSION}/'"${QUARKUS_VERSION}"'/g' ./modules/testcases/jdkAll/Protean/basic/upstream/test-configurations/pom.xml
./Maven/apache-maven-3.5.3/bin/mvn clean install -Dquarkus  -Dnative -DJBOSS_VERSION=${JBOSS_VERSION}
sed -i 's/<version>'"${QUARKUS_VERSION}"'<\/version>/<version>${QUARKUS_VERSION}<\/version>/g' ./modules/testcases/jdkAll/Protean/basic/upstream/test-configurations/pom.xml

