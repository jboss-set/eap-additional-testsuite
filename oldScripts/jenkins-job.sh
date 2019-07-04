if [ -z "${LOCAL_REPO_DIR}" ]; then
  echo "Missing LOCAL_REPO_DIR - please provide value to the local maven repo to use"
  exit 1
fi

if [ -z "${JBOSS_FOLDER}" -a ! -d "${JBOSS_FOLDER}" ]; then
  echo "Missing JBOSS_FOLDER variable or not pointing to a directory: ${JBOSS_FOLDER}"
  exit 2
fi

if [ -z "${JBOSS_VERSION}" ]; then
  echo "Missing JBOSS_VERSION - please provide JBOSS AS version used."
  exit 3
fi

if [ -z "${JBOSS_VERSION_CODE}" ]; then
  echo "Missing JBOSS_VERSION_CODE (eap7, eap64,...)."
  exit 4
fi

export MAVEN_OPTS="-Xmx1024m -Xms512m -XX:MaxPermSize=256m"

# remove previous build
rm -r -f eap*

mvn clean install -D${JBOSS_VERSION_CODE} -Dstandalone -Dmaven.repo.local=${LOCAL_REPO_DIR}
