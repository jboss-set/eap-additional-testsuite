# EAP ADDITIONAL TESTSUITE
--------------------------
## THE JBOSS TESTSUITE TO DEVELOP TESTS AGAINST INFINITE NUMBER OF JBOSS SERVERS
--------------------------------------------------------------------------------
## A PROJECT UNDER THE ΙΔΕΑ STATEMENT
--------------------------------------
An additional JBOSS testsuite in order to facilitate QE.

Write your tests once and run them against any version of EAP and WILDFLY application server.



In order to synchronize the dependency versions of eap-additional-testsuite with the component versions of the equivalent server you can run the additional-testsuite using -Dstandalone or -Dserver-integration property.

Standalone Mode
---------------
If you enable standalone mode (-Dstandalone), please make sure that the server parent pom is available locally or remotely as an artifact.

Server-Integration Mode
---------------
If you enable server-integration mode (-Dserver-integration), please make sure that the server BOM is available locally or remotely as an artifact.



Testing EAP
-----------
1. Make sure that JBOSS_FOLDER environment variable is set with the path to your JBOSS EAP directory.
2. Make sure that JBOSS_VERSION environment variable is set with the version of JBOSS EAP Server.
3. Build and run the additional testsuite activating the EAP profile (-Deap -Dstandalone).
4. If you want to use http add -Dmaven.repository.protocol=http (Section : Using HTTPS below)
5. If you would like/need to disable snapshot versions add -DDISABLE_SNAPSHOT_VERSIONS=true


Testing Wildfly
---------------
1. Make sure that JBOSS_FOLDER environment variable is set with the path to your WILDFLY directory.
2. Make sure that JBOSS_VERSION environment variable is set with the version of WILDFLY Server.
3. Build and run the additional testsuite activating the WILDFLY profile (-Dwildfly -Dstandalone).
4. If you want to use http add -Dmaven.repository.protocol=http (Section : Using HTTPS below)
5. If you would like/need to disable snapshot versions add -DDISABLE_SNAPSHOT_VERSIONS=true


Testing EAP or Wildfly with specific JDK version
------------------------------------------------
1. Make sure that JBOSS_FOLDER is set with the path to your EAP OR WILDFLY directory.
2. Make sure that JBOSS_VERSION environment variable is set with the version of JBOSS EAP OR WILDFLY Server.
3. Make sure that JAVA_HOME is pointing to the jdk of desired version.
4. Build and run the additional testsuite activating the EAP or WILDFLY specific jdk version profile (-Deap-jdk8, -Dwildfly-jdk8).
5. If you want to use http add -Dmaven.repository.protocol=http (Section : Using HTTPS below)
6. If you would like/need to disable snapshot versions add -DDISABLE_SNAPSHOT_VERSIONS=true


Using HTTPS
-----------
After changing from HTTP to HTTPS, the maven repositories for Eap may not host a valid public SSL certificate, which will lead to maven failed to build the project. There are 3 options to fix that if it happens:

1. Specify the -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true in the maven build command line to bypass the SSL certificates check (This is suitable for local workstation case)
2. Specify -Dmaven.repository.protocol=http to switch back to HTTP
3. Import the server certificate into your local trusted store.


ADVANTAGES 
----------
1. Writing the tests once and testing against infinite number of Application Servers.
2. Having all the tests at one place.
3. Comparison of the servers based on the testsuite.
4. Guarding against regression.
5. Faster convergence among the servers.
6. Comparison of the servers based on tests of the past and the present.
7. Addition of tests with possible future features that are not at the moment available.
8. It makes possible to push a testcase of a fix regarding a specific component of the server, without the component version to have been updated at the server pom.
9. Ability to merge tests from remote testsuites.

 
MOTIVATION
----------
If a test is developed for server X then it can be automatically tested against all the other servers.

 
HOW TO DEBUG (for testable deployments and non-marked as @RunAsClient tests)
---------------------------------------
1. Start the server (that you have defined in JBOSS_FOLDER) with the --debug 'port' option.
2. Connect the Debugger to the Remote VM inside the server using some IDE.
3. Activate the equivalent debug profile at your IDE (eg eap7.testsuite).
4. Set your breakpoints.
5. And then start the debugging executing the specific test which is errorous.


HOW TO DEBUG (for non-testable deployments and marked as @RunAsClient tests)
---------------------------------------
1. Activate the equivalent debug profile at your IDE (eg eap7.testsuite).
2. Set your breakpoints.
3. Run your tests from command line adding -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8787 -Xnoagent -Djava.compiler=NONE" property. 
e.g. mvn -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8787 -Xnoagent -Djava.compiler=NONE" test -Dwildfly -Dstandalone
4. Connect the Debugger to the Remote VM using some IDE.
5. Debug the specific test which is errorous.


HOW TO BUILD EAP ADDITIONAL TESTSUITE INSIDE AN IDE (ECLIPSE, IntelliJ IDEA, NETBEANS)
--------------------------------------------------------------
1. Before starting the IDE, go to your IDE directory and export JBOSS_FOLDER and JBOSS_VERSION environment variables at file /etc/environment and execute the command "source /etc/environment".
2. Start your IDE from command line and load the project.
3. Go to Modules directory and activate the desired server profile (eg wildfly.testsuite). Then activate the standalone profile at the server dir that has appeared.
4. Make sure that the server parent pom is available locally or remotely as an artifact.
5. Modify the run/build configurations in order to use the the desired goals, profiles and environment variables 
6. Build your project.


HOW TO PROCESS THE SOURCES INSIDE AN IDE
----------------------------------------
In order to process the eap-additional-testsuite sources, please, after following the steps of the section **"HOW TO BUILD EAP ADDITIONAL TESTSUITE INSIDE AN IDE (ECLIPSE, IntelliJ IDEA, NETBEANS)"** (activating the equivalent server profile and the standalone profile, etc), proceed to the following actions depending on the way of processing and the IDE that you choose :

**1st WAY OF PROCESSING (This way applies for all the IDEs)**

1. Load the subcategory level where exist the tests that you would like to process.
2. Process your sources.
3. Do not forget to copy the modified sources at the Modules Level (modules/src/main/java). In a different case, if you run the project from the Parent Level, your modified sources will be overriden by the ones that exist at the Modules Level (Risk of losing your modifications).

**2nd WAY OF PROCESSING**

Please, **go to the Server Level** (e.g. [Wildfly Server Level](https://github.com/jboss-set/eap-additional-testsuite/tree/master/modules/testcases/jdkAll/Wildfly)) and follow the actions depending on the IDE:

* **Eclipse :** 
  * Go to the Server Level Project -> Properties -> Java Build Path. Under the Source Tab click the Link Source Button. Linked folder location should be "PATH_TO_PROJECT/eap-additional-testsuite/modules/src/main/java" and "Update exclusion filters in other source folders to solve nesting" should be chosen. Apply the changes. The sources should be, now, visible in your IDE to process. 
  * The .project file should look like :
```xml
<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
	<name>test</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
		<buildCommand>
			<name>org.eclipse.m2e.core.maven2Builder</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
		<nature>org.eclipse.jdt.core.javanature</nature>
		<nature>org.eclipse.m2e.core.maven2Nature</nature>
	</natures>
	<linkedResources>
		<link>
			<name>java</name>
			<type>2</type>
			<location>"PATH TO THE LINKED SOURCE FILES"</location>
		</link>
	</linkedResources>
</projectDescription>
```
  * Add the JRE System Library in your project.
  * Add the following lines in the .classpath file : 
```xml 
<classpathentry exported="true" kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
<classpathentry exported="true" kind="con" path="org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER">
		<attributes>
			<attribute name="maven.pomderived" value="true"/>
		</attributes>
</classpathentry>
```

* **IntelliJ IDEA :** Go to the Server Level Project -> Project Structure -> Sources, remove the current context root and add "PATH_TO_PROJECT/eap-additional-testsuite/modules/src/main/java" as the context root. Apply the changes. The sources should be, now, visible in your IDE to process under "java" directory. 

* **Netbeans :** The sources should be visible in your IDE to process.

**Important note :** 

1. If the sources are to be modified or extended, the working directory of the run/build configuration should be set to the eap-additional-testsuite parent directory, in order that any changes in the sources would be propagated to the specific server sources.
2. The sources to be processed should be the ones linked to modules/src/main/java directory.


EAP-ADDITIONAL-TESTSUITE INTEGRATED INSIDE THE SERVERS (HOW TO USE)  - AVAILABLE SOON
-------------------------------------------------------------------------------------
1. Send your PR containing the test to eap-additional-testsuite.
2. Before sending the server PR inform the server that you would like to include a specific PR of eap-additional-testsuite, adding the following lines in git-sub-modules/git-sub-module.sh : 
    - git submodule update --init --remote
    - cd ../testsuite/addtional-testsuite/eap-additional-testsuite/
    - git fetch origin refs/pull/8/head && git checkout FETCH_HEAD (declare that you would like to include PR 8 of eap-additional-testsuite)
3. Commit the changes on the server and send the server PR.
4. The specific PR of the test will be tested along with the server PR.


EXAMPLE OF USAGE 
-----------------
Supposing that we would like to add Mtom TestCase for Wildfly (master) and Eap 7 (7.x) Servers in Eap Additional Testsuite, we would follow the steps bellow :

1. Add the source code of the test in [modules/src/main directory](https://github.com/jboss-set/eap-additional-testsuite/tree/master/modules/src/main/java/org/jboss/additional/testsuite/jdkall/present/web/mtom) (both for Wildfly and Eap7 - write your code once) and annotate all the classes used in the test with the @EapAdditionalTestsuite annotation, specifing the locations of the Servers that the classes will be distributed to, as a String Array attribute (e.g. [@EapAdditionalTestsuite Example](https://github.com/jboss-set/eap-additional-testsuite/blob/master/modules/src/main/java/org/jboss/additional/testsuite/jdkall/present/web/mtom/MtomTestCase.java#L50)).

2. Add the resources in [Wildfy Mtom recource directory](https://github.com/jboss-set/eap-additional-testsuite/tree/master/modules/testcases/jdkAll/Wildfly/web/test-configurations/src/test/resources) (for Wildfly) and  [Eap7 Mtom recource directory](https://github.com/jboss-set/eap-additional-testsuite/tree/master/modules/testcases/jdkAll/Eap7/web/test-configurations/src/test/resources) (for Eap7)

3. Add any additional dependencies in [Wildfy pom](https://github.com/jboss-set/eap-additional-testsuite/blob/master/modules/testcases/jdkAll/Wildfly/pom.xml)  (for Wildfly) and [Eap7 pom](https://github.com/jboss-set/eap-additional-testsuite/blob/master/modules/testcases/jdkAll/Eap7/pom.xml) (for Eap7)

4. Enable the Mtom TestCase in [Wildfy Web Configuration pom](https://github.com/jboss-set/eap-additional-testsuite/blob/master/modules/testcases/jdkAll/Wildfly/web/test-configurations/pom.xml) (for Wildfly) and [Eap7 Web Configuration pom](https://github.com/jboss-set/eap-additional-testsuite/blob/master/modules/testcases/jdkAll/Eap7/web/test-configurations/pom.xml) (for Eap7)

5. Build the Eap Additional Testsuite for Wildfy and Eap7 (having set the equivalent JBOSS_FOLDER and JBOSS_VERSION environment variables) :
    - mvn clean install -Dwildfly -Dstandalone (for Wildfly)
    - mvn clean install -Deap7 -Dstandalone (for Eap7)
    
 
OTHER FEATURES
--------------
1. Infinite number of servers could be added in Eap-Additional-Testsuite, while the tests included could be tested against all these servers.
2. Eap-Additional-Testsuite could be a very helpful tool in combination with the documentation. In parallel with the documentation, examples of use cases could be provided, which could be tested against all the servers.
3. Eap-Additional-Testsuite is the first component to change the relation of Component -> Server to Component <-> Server, as this component can be considered as a part of a Server, but also a Server can be considered as a part of Eap-Additional-Testsuite.
4. The AT Structures can be used with any Software Program language.


I NEED ASSISTANCE TO ADD A NEW TEST
-----------------------------------
If any assistance is needed to add a new test in Eap Additional Testsuite, please add it to the following directories, denoting the versions of the servers within the tests should be tested (eg EAP 7.1.1.DR1, etc) :

1. https://github.com/jboss-set/eap-additional-testsuite/tree/master/modules/src/main/java/org/jboss/additional/testsuite/jdkall/IwantToAddAnewTest
2. https://github.com/jboss-set/eap-additional-testsuite/tree/master/modules/src/main/java/org/jboss/additional/testsuite/jdk8/IwantToAddAnewTest


HOW TO MERGE TESTS FROM REMOTE TESTSUITES
-----------------------------------------
1. Set the GITHUB_REPO environment variable with the url of the remote testsuite repo (e.g. export GITHUB_REPO=https://github.com/panossot/RemoteTestRepo.git).
2. Set the SUBDIR environment variable with the subdirectory path of the repo you would like to copy to modules/src/main/java direcory of EAT (e.g. export SUBDIR=RemoteTestRepo/src/java/main/org).
3. Run EAT as usual.


INNOVATION
-----------------------------------
The innovative part of EAT is creating the test once and testing with any version of the tested software. It may be firstly applied for the JBOSS Servers, but, in general, a similar structure, can be used for creating tests about any software with multiple versions or for multiple software programs that have a part of the testsuite in common.

Another innovative part of the AT structure is that using the annotation @ATFeature, it can create dynamic subsets of tests in order to test dynamically created software programs (using provisioning tools) according to a specific list of features / characteristics.


FUTURE DEVELOPMENT
-----------------------------------
1. Use the generalized additional testsuite structure in order to include Fedora and Openshift tests for multiple versions of Fedora and Openshift.
2. Create different categories of ATs (Additional Testsuites) : e.g. ATs for features could be different from ATs for bugs.
3. Use AT structures for testing containers (e.g. using Arquillian cube)
4. Create a framework that will combine all ATs (Additional Testsuites), e.g. EAT, JBTAT, etc. The user would be able to add a test to whichever testsuite he/she would choose. Furthermore, the user should be able to add more ATs in this framework, according to the software project of usage.
5. The past sources could contain only the differences.
6. Distribution of tests based on if a specific commit is merged.
7. The servers could build only the modules related to the proposed modification changes.
8. Make it more user friendly (for whoever is not familiar with its usage), using a pre-entry step, by adding a testcase, as usual, to a single-version server pre-testsuite. Then the test case will be added in the Eap-Additional-Testsuite sources for multiversion usage.
9. Create a subcategory for a collection of components.
10. Show how it can test multi-versioned software with multiple components that are dynamically updated in real time using component chains. 
11. NEXT GENERATION AT STRUCTURES : The tests/methods (that may use features/dependencies from a provisioning tool/dependency-feature analyzer) should be independent modules that will have dependencies (metadata could be used) on libraries and other methods (which may also use some features/dependencies from the provisioning tool/dependency-feature analyzer and which could have their own dependencies). The tool should compose the appropriate test cases and distribute them to the appropriate subcategories in accordance with the available features/dependencies. The dependencies could also be defined automatically when using a dependency-feature analyzer. This way the annotations for enabling the tests could be automated (and become non mandatory in some cases).


MOJO DOCUMENTATION
------------------
[Eap Additional Testsuite Mojo Document](https://mojo.redhat.com/docs/DOC-1063426)


EAT with PROTEAN/QUARKUS DOCUMENTATION
--------------------------------------
[Eap Additional Testsuite with Protean/Quarkus](https://mojo.redhat.com/docs/DOC-1191712)
 
 
EAP-ADDITIONAL-TESTSUITE PRESENTATION
-------------------------------------
[Eap Additional Testsuite Presentation](http://redhat.slides.com/panossot/eap-wildfly-additional-testsuite-5?token=cJjcvl_r)

[EAT JAM](https://redhat.slides.com/panossot/eap-wildfly-additional-testsuite-5-8-11?token=p6VY95LJ)


EAP-ADDITIONAL-TESTSUITE VIDEO PRESENTATION
-------------------------------------------
[Eap Additional Testsuite Video](https://bluejeans.com/s/A_Ih7)

[Eap Additional Testsuite Quick Presentation](https://drive.google.com/open?id=1bkgdVZ8AWDwkG4XSDr0s3tX1zKKLtoQp)


EAP-ADDITIONAL-TESTSUITE WORKSHOP
---------------------------------
[Eap Additional Testsuite Workshop](https://www.dropbox.com/s/bebhyd1iz7cg1i2/EAT_WORKSHOP.odt?dl=0)

[Eap Additional Testsuite Workshop (Download)](https://drive.google.com/file/d/1iRT_FoRxj2tOcIPD42mUQpqcbDxuczCy/view?usp=sharing)


EAP-ADDITIONAL-TESTSUITE as a COLLABORATION PROJECT
---------------------------------------------------
Many thanks to Carlo de Wolf for his ideas and help and for including EAT under the JBoss-SET umbrella.
Also, many thanks to the University of Peloponnese (Department of Informatics and Telecommunications) for the theory of AT Structures.

# License 
* [GNU Lesser General Public License Version 2.1](http://www.gnu.org/licenses/lgpl-2.1-standalone.html) ([GPL Cooperation Commitment](https://github.com/gplcc/gplcc/blob/master/Project/COMMITMENT))

