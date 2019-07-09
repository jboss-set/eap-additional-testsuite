#!/bin/bash

set -eo pipefail

readonly JBOSS_VERSION_CODE=${1}
readonly SMODE=${2}

readonly NAME_PREFIX=${NAME_PREFIX:-'jboss-eap'}
readonly SETTINGS_XML=${SETTINGS_XML-"$(pwd)/settings.xml"}

readonly MAVEN_CACHE_SERVER=${MAVEN_CACHE_SERVER:-$(hostname)}

set -u

usage() {
  local script_name=$(basename ${0})

  echo "${script_name} <jboss-version-code> <smode>"
  echo
  echo "Ex: ${script_name} wildfly"
  echo
  echo "On top of those arguments, this script excepts the following env vars to be set:"
  echo '- MAVEN_HOME, set to the appropriate directory containing the Maven distribution'
  echo ''
  echo 'The following parameter can be overridden if needed:'
  echo '- JBOSS_VERSION, set the version used for labelling jar dependencies associate to the AS version.'
  echo '- NAME_PREFIX, default to 'jboss-eap' if not specified.'
  echo '- MAVEN_LOCAL_REPOSITORY, path to a local mave repository to use for dependencies.'
  echo '- SETTINGS_XML, path to custom settings.xml, default to ./settings.xml; Ignored if set to an empty value'
}

assertJBossASVersion() {
  local pom_file_prefix=${1}
  local m2_repo=${2}

  cd "${m2_repo}"
  local pom_file=$(find . -name "${pom_file_prefix}-parent*.pom")

  if [ ! -e "${pom_file}" ]; then
    echo "No pom file found with prefix ${pom_file_prefix} in ${m2_repo}."
    echo "Cannot infer project version, please set JBOSS_VERSION env var."
    exit 6
  fi

  which xpath 2>&1 > /dev/null
  if [ "${?}" -ne 0 ]; then
    echo 'Utility 'xpath' is missing from PATH. Please install tool or set JBOSS_VERSION.'
    exit 7
  fi

  xpath "${pom_file}" '//project/version' 2> /dev/null | sed -e 's;</*version>;;g'
}

if [ -z "${JBOSS_VERSION_CODE}" ]; then
  echo "Missing JBOSS_VERSION_CODE (eap7, eap64,...)."
  usage
  exit 1
fi

if [ -z "${MAVEN_HOME}" ]; then
  echo "No MAVEN_HOME has been defined."
  exit 2
fi

if [ ! -e "${MAVEN_HOME}" ]; then
  echo "Provided MAVEN_HOME does not exist: ${MAVEN_HOME}"
  exit 3
fi

if [ ! -d "${MAVEN_HOME}" ]; then
  echo "Provided MAVEN_HOME is not a directory: ${MAVEN_HOME}"
  exit 4
fi

#
# Set build configuratin (settings.xml, local maven repository) and
# Generating distribution specific value (JBOSS_VERSION and JBOSS_FOLDER)
#

if [ -e "${SETTINGS_XML}" ]; then
  readonly SETTINGS_XML_OPTION="-s ${SETTINGS_XML}"
   # see https://projects.engineering.redhat.com/browse/SET-126
  sed -i  "${SETTINGS_XML}" \
      -e "s;MAVEN_CACHE_SERVER;${MAVEN_CACHE_SERVER};"

else
  readonly SETTINGS_XML_OPTION=''
fi

readonly MAVEN_LOCAL_REPOSITORY=${MAVEN_LOCAL_REPOSITORY:-"$(pwd)/maven-local-repository"}
if [ -d "${MAVEN_LOCAL_REPOSITORY}" ]; then
  readonly MAVEN_LOCAL_REPOSITORY_OPTION="-Dmaven.repo.local=${MAVEN_LOCAL_REPOSITORY}"
else
  readonly MAVEN_LOCAL_REPOSITORY_OPTION=''
fi

readonly JBOSS_VERSION=${JBOSS_VERSION:-$(assertJBossASVersion "${NAME_PREFIX}" "${MAVEN_LOCAL_REPOSITORY}")}
export JBOSS_VERSION

readonly JBOSS_FOLDER=${JBOSS_FOLDER:-"$(pwd)/dist/target/${NAME_PREFIX}-${JBOSS_VERSION}"}
export JBOSS_FOLDER

if [ ! -d "${JBOSS_FOLDER}" ]; then
  echo "The folder provided for JBoss AS server using the JBOSS_FOLDER env  is not a directory: ${JBOSS_FOLDER}."
  exit 5
fi

if [ ! -z "${SMODE}" ]; then
  SMODE_CONFIG='-Dserver-integration'
else
  SMODE_CONFIG='-Dstandalone'
fi

#
# Setting up maven
#

if [ -d "${MAVEN_HOME}/bin" ]; then
  export MAVEN_HOME
  export PATH="${MAVEN_HOME}/bin:${PATH}"
fi

export MAVEN_OPTS=${MAVEN_OPTS:-''}
export MAVEN_OPTS="${MAVEN_OPTS} -Dmaven.wagon.http.pool=false"
export MAVEN_OPTS="${MAVEN_OPTS} -Dmaven.wagon.httpconnectionManager.maxPerRoute=3"
export MAVEN_OPTS="${MAVEN_OPTS} -Xmx1024m -Xms512m -XX:MaxPermSize=256m"

#
# Run EAT
#
echo "Runing EAT on JBoss server: ${JBOSS_FOLDER}  ${EAT_EXTRA_OPTS}"
mvn clean install -Djdk8=true -D${JBOSS_VERSION_CODE} ${SMODE_CONFIG} ${SETTINGS_XML_OPTION} ${MAVEN_LOCAL_REPOSITORY_OPTION} ${EAT_EXTRA_OPTS}

