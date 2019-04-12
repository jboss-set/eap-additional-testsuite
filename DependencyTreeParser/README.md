# EAP ADDITIONAL TESTSUITE
--------------------------
## THE JBOSS TESTSUITE TO DEVELOP TESTS AGAINST INFINITE NUMBER OF JBOSS SERVERS
--------------------------------------------------------------------------------
## A PROJECT UNDER THE ΙΔΕΑ STATEMENT
--------------------------------------

Starting the development of a first implementation of the dependency schema of AT Structures for EAT.

This subdirectory is used for finding the packages that are used / loaded by the JBoss Servers and the packages that are used via the test cases.


1. ** Go to the server parent directory and build the server : mvn clean install -DskipTests . Also, execute : mvn dependency:tree > output.txt ** 
2. export DependencyTreeFilePath=path to the output.txt file
3. export ExternalDependencyPath=path to the another file listing the external dependencies / testsuite dependencies that are already downloaded in the local repo (line format : new.artifact:new.id:jar:new.version). In order to download apriori the local testsuite dependencies, the -DnoDistribution parameter of EAT can be used.
4. export ExcludedDependenciesPath=path to the another file listing the excluded libraries (parend dir of library) that we know that are not provided by the server dependency lists (line format : org.jboss.eap.additional.testsuite.annotations)
5. export MavenRepoPath=path to the local maven repository
6. export BaseDir=the path to the eap-additional-testsuite dir
7. export SourcePath=the path to the dir of sources
8. export Server=the server name of the test subset that is aimed to be used
9. export Version=the version of the server that will be tested
10. export VersionOrderDir=versionOrder
11. Then go to this current DependencyTreeParser directory and execute : mvn clean install (This command will display all the packages being used/loaded in the maven repo)

** During step 1, in case the server bom-pom.xml, produced in component-matrix-builder/target path of the server, should be used, it should be firstly modified to a pom.xml file with all dependencies outside the <dependencymanagement> block (excluding the dependencies that are not available at the remote repos). Then, using this pom, we produce the output.txt file using the command : mvn dependency:tree > output.txt
