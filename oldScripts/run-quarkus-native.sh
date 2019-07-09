#!/bin/bash
readonly NATIVE=${NATIVE:-''} # export NATIVE=-Pnative

sed -i 's/${QUARKUS_VERSION}/'"${QUARKUS_VERSION}"'/g' ./modules/testcases/jdkAll/Protean/quarkus/quark/test-configurations/pom.xml
./Maven/apache-maven-3.5.3/bin/mvn clean install -Dquarkus "${NATIVE}"-DJBOSS_VERSION=${JBOSS_VERSION} -Dquarkus-project-discovery=false -Dquarkus-cp-cache=false -Dquarkus-workspace-discovery=false -Dquarkus-classpath-cache=false
sed -i 's/<version>'"${QUARKUS_VERSION}"'<\/version>/<version>${QUARKUS_VERSION}<\/version>/g' ./modules/testcases/jdkAll/Protean/quarkus/quark/test-configurations/pom.xml

