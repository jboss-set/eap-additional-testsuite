#!/bin/bash

set -e

readonly LOCAL_REPO_DIR=${1}
readonly JBOSS_VERSION_CODE=${2}
readonly SETTINGS_XML=${SETTINGS_XML:-"$(pwd)/settings.xml"}

readonly GIT_REPO=${GIT_REPO:-'git@github.com:wildfly/wildfly.git'}
readonly GIT_BRANCH=${GIT_BRANCH:-'master'}
readonly CHECKOUT_FOLDER=${CHECKOUT_FOLDER:-"${PWD}/${JBOSS_VERSION_CODE}"}

usage() {
  local script_name=$(basename ${0})

  echo "${script_name} <local-maven-repo> <jboss-version-code>"
  echo
  echo "Ex: ${script_name} ./maven_local_repo wildfly"
  echo
  echo "On top of those arguments, this script excepts the following env vars to be set:"
  echo "- MAVEN_HOME, set to the appropriate directory containing the Maven distribution: ${MAVEN_HOME}."
  echo "- GIT_REPO, git repository to use, default to '${GIT_REPO}'."
  echo "- GIT_BRANCH, branch to use in the repository, default to '${GIT_BRANCH}'."
  echo "- JBOSS_FOLDER, path to the server built in the previous repository, default to dist/target from git repository."
  echo
  echo 'The scripts also has the following extra parameters:'
  echo '- BUILD_FROM_SOURCE, to rebuild server from source.'
  echo '- SETTINGS.XML, to specify maven to use (or not) a specific settings.xml'
}

if [ -z "${LOCAL_REPO_DIR}" ]; then
  echo "Missing LOCAL_REPO_DIR - please provide value to the local maven repo to use"
  usage
  exit 1
fi

if [ -z "${JBOSS_VERSION_CODE}" ]; then
  echo "Missing JBOSS_VERSION_CODE (eap7, eap64,...)."
  usage
  exit 2
fi

#
# Checking out Wildfly/EAP
#
if [ -d "${CHECKOUT_FOLDER}" ]; then
  echo "Folder already exists - updating local repository"
  cd "${CHECKOUT_FOLDER}"
  git pull origin "${GIT_BRANCH}"
  cd - > /dev/null
else
  git clone "${GIT_REPO}" --branch "${GIT_BRANCH}" "${CHECKOUT_FOLDER}"
fi

#
# Setting up maven
#

if [ -z "${MAVEN_HOME}" ]; then
  echo "No MAVEN_HOME has been defined."
  usage
  exit 3
fi

if [ ! -e "${MAVEN_HOME}" ]; then
  echo "Provided MAVEN_HOME does not exist: ${MAVEN_HOME}"
  usage
  exit 4
fi

if [ ! -d "${MAVEN_HOME}" ]; then
  echo "Provided MAVEN_HOME is not a directory: ${MAVEN_HOME}"
  usage
  exit 5
fi

if [ -e "${SETTINGS_XML}" ]; then
  readonly SETTINGS_XML_OPTION="-s ${SETTINGS_XML}"
fi

mkdir -p "${LOCAL_REPO_DIR}"

export MAVEN_HOME
export PATH="${MAVEN_HOME}/bin:${PATH}"

export MAVEN_OPTS="${MAVEN_OPTS} -Dmaven.wagon.http.pool=false"
export MAVEN_OPTS="${MAVEN_OPTS} -Dmaven.wagon.httpconnectionManager.maxPerRoute=3"
export MAVEN_OPTS="${MAVEN_OPTS} -Xmx1024m -Xms512m -XX:MaxPermSize=256m"

#
# Building Wildfly/EAP
#

cd "${CHECKOUT_FOLDER}"
export JBOSS_VERSION="$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version ${SETTINGS_XML_OPTION} | grep -Ev '(^\[|Download\w+:)')"
if [ -n "${BUILD_FROM_SOURCE}" ]; then
  mvn clean install -DskipTests -Dmaven.repo.local=${LOCAL_REPO_DIR} ${SETTINGS_XML_OPTION}
fi
cd ..

export JBOSS_FOLDER=${JBOSS_FOLDER:-$(ls -d ${CHECKOUT_FOLDER}/dist/target/*-${JBOSS_VERSION})}
if [ ! -d "${JBOSS_FOLDER}" ]; then
  echo "The folder provided for JBoss AS server using the JBOSS_FOLDER env  is not a directory: ${JBOSS_FOLDER}."
  ls ${CHECKOUT_FOLDER}/dist/target
  exit 6
fi

#
# Run EAT
#
echo "Runing EAT on JBoss server: ${JBOSS_FOLDER}"
mvn clean install -D${JBOSS_VERSION_CODE} -Dstandalone -Dmaven.repo.local=${LOCAL_REPO_DIR} ${SETTINGS_XML_OPTION}
