# JBOSS ADDITIONAL TESTSUITE
============================
An additional JBOSS testsuite in order to facilitate QE.

Write your tests once and run them on both EAP and WILDFLY application server.


Testing EAP 7.x
----------------
1. Make sure that JBOSS_FOLDER environment variable is set with the path to your JBOSS EAP directory.
2. Build and run the additional testsuite activating the EAP 7 profile (-Deap7).


Testing EAP 6.4.x
------------------
1. Make sure that JBOSS_FOLDER environment variable is set with the path to your JBOSS EAP directory.
2. Build and run the additional testsuite activating the EAP profile (-Deap).


Testing Wildfly
----------------
1. Make sure that JBOSS_FOLDER environment variable is set with the path to your WILDFLY directory.
2. Build and run the additional testsuite activating the WILDFLY profile (-Dwildfly).


Testing EAP or Wildfly with specific JDK version
------------------------------------------------
1. Make sure that JBOSS_FOLDER is set with the path to your EAP OR WILDFLY directory.
2. Make sure that JAVA_HOME is pointing to the jdk of desired version.
3. Build and run the additional testsuite activating the EAP or WILDFLY specific jdk version profile (-Deap-jdk8, -Dwildfly-jdk8).


ADVANTAGES
----------
- Having all the tests at one place.
- Comparison of the servers based on the testsuite.
- Guarding against regression.


MOTIVATION
----------
If a test is developed for server X then it can be automatically tested against all the other servers.


FUTURE EXTENSIONS
-----------------
Use it with every server as a git submodule.


PROCEDURE TO FOLLOW WHEN THE GIT SUBMODULES ARE ACTIVATED ON THE SERVERS
------------------------------------------------------------------------
1. Add your test at Eap Additional Testsuite and create a PR.
2. Add your code development/fix at the server and include your eap-additional-testsuite PR as follows :
```script
   - cd testsuite (go to the testsuite directory of the server)
   - git submodule update --init --remote (update your submodule)
   - cd eap-additional-testsuite (go to the eap-additinal-testsuite directory of the server)
   - git fetch origin refs/pull/NUM/head && git checkout FETCH_HEAD
   - cd ../..
   - Create your PR to the server including the updated hash of the eap-additional-testsuite submodule.
```

#License 
* [GNU Lesser General Public License Version 2.1](http://www.gnu.org/licenses/lgpl-2.1-standalone.html)

