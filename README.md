# JBOSS ADDITIONAL TESTSUITE
============================
An additional JBOSS testsuite in order to facilitate QE.

Write your tests once and run them on both EAP and WILDFLY application server.

Testing EAP 7.x
-----------
1. Make sure that JBOSS_FOLDER environment variable is set with the path to your JBOSS 7.x EAP directory.
2. Build and run the additional testsuite activating the EAP 7.x profile (-Deap7).

Testing EAP 6.4.x
-----------
1. Make sure that JBOSS_FOLDER environment variable is set with the path to your JBOSS 6.4.x EAP directory.
2. Build and run the additional testsuite activating the EAP 6.4.x profile (-Deap64x).

Testing EAP 6.3.x
-----------
1. Make sure that JBOSS_FOLDER environment variable is set with the path to your JBOSS 6.3.x EAP directory.
2. Build and run the additional testsuite activating the EAP 6.3.x profile (-Deap63x).

Testing Wildfly
---------------
1. Make sure that JBOSS_FOLDER environment variable is set with the path to your WILDFLY directory.
2. Build and run the additional testsuite activating the WILDFLY profile (-Dwildfly).

Testing EAP or Wildfly with specific JDK version
------------------------------------------------
1. Make sure that JBOSS_FOLDER is set with the path to your EAP OR WILDFLY directory.
2. Make sure that JAVA_HOME is pointing to the jdk of desired version.
3. Build and run the additional testsuite activating the EAP or WILDFLY specific jdk version profile (-Deap7-jdk8, -Dwildfly-jdk8).


#License 
* [GNU Lesser General Public License Version 2.1](http://www.gnu.org/licenses/lgpl-2.1-standalone.html)

