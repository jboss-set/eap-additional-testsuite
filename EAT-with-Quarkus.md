# EAP ADDITIONAL TESTSUITE
--------------------------
## THE JBOSS TESTSUITE TO DEVELOP TESTS AGAINST INFINITE NUMBER OF JBOSS SERVERS
--------------------------------------------------------------------------------
## A PROJECT UNDER THE ΙΔΕΑ STATEMENT
--------------------------------------
## EAT with QUARKUS
--------------------------------

EAT can, now, be used to distribute the tests not only to the appropriate version of the server to be tested with,
but it can test the microprofile applications. It can also be used with Quarkus in both native and no-native mode, while the code (or part of the code of the tests) can be distributed for both server and Quarkus app testing.

 

How to test EAT with Quarkus 
-------------------------------------
 
1. Build Quarkus in order to produce the products of the built in the maven repo.
2. export GRAALVM_HOME=.../graalvm-ce-1.0.0-rcX-linux-amd64/graalvm-ce-1.0.0-rcX (path to GraalVM)
3. export JBOSS_VERSION = the quarkus version
4. export QUARKUS_VERSION = the version of Quarkus that we have built at step 1
5. ./run-quarkus-non-native.sh (for the non-native mode) or ./run-quarkus-native.sh (for the native mode)

Note : In general JBOSS_VERSION = QUARKUS_VERSION . In case of the master branch, where the version in not in the format x.y.z (999-SNAPSHOT), QUARKUS_VERSION should be set to 999-SNAPSHOT (the branch version) and JBOSS_VERSION to 999.0.0-SNAPSHOT version (in format x.y.z) .

 
 

Testing with the master version :
---------------------------------
export JBOSS_VERSION = 999.0.0-SNAPSHOT

export QUARKUS_VERSION = 999-SNAPSHOT

./run-quarkus-native.sh

 

Testing with 0.11.0 version :
-----------------------------
export JBOSS_VERSION = 0.11.0

export QUARKUS_VERSION = 0.11.0

./run-quarkus-native.sh

 

Testing with 0.10.0 version :
-----------------------------
export JBOSS_VERSION = 0.10.0

export QUARKUS_VERSION = 0.10.0

./run-quarkus-native.sh

 

Testing with 0.9.1 version :
----------------------------
export JBOSS_VERSION = 0.9.1

export QUARKUS_VERSION = 0.9.1

./run-quarkus-native.sh


