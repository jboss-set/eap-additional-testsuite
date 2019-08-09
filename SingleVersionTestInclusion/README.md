# EAP ADDITIONAL TESTSUITE SINGLE VERSION TESTS
-----------------------------------------------

For those who would like to add a test in Eap Additional Testsuite, quickly, in the same way a common test is added in the regular-existing repositories, he/she could add the test here (SingleVersionTestInclusion directory) commenting the versions of the server he/she would like to test. Then the test will be added along with the other eap-additional-testsuite sources in order to enable multiversion testing.

1. cd SingleVersionTestInclusion
2. Go to eap or wildfly directory depending where you would like to add the testcase
3. Replace JBOSS_FOLDER and JBOSS_VERSION in the pom file with the server dist directory and the version of the server equivalently (e.g. For wildfly server set JBOSS_FOLDER to the path of wildfly/dist/target/wildfly-18.0.0.Beta1-SNAPSHOT' and JBOSS_VERSION to version 18.0.0.Beta1-SNAPSHOT in the pom.xml. Similarly for eap server.)
4. mvn clean install


# License 
* [GNU Lesser General Public License Version 2.1](http://www.gnu.org/licenses/lgpl-2.1-standalone.html) ([GPL Cooperation Commitment](https://github.com/gplcc/gplcc/blob/master/Project/COMMITMENT))

