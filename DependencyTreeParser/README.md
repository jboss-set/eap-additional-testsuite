# EAP ADDITIONAL TESTSUITE
--------------------------
## THE JBOSS TESTSUITE TO DEVELOP TESTS AGAINST INFINITE NUMBER OF JBOSS SERVERS
--------------------------------------------------------------------------------
## A PROJECT UNDER THE ΙΔΕΑ STATEMENT
--------------------------------------

Starting the development of a first implementation of the dependency schema of AT Structures for EAT.

This subdirectory is used for finding the packages that are uses / loading by the JBoss Servers.


1. Go to the server parent directory and execute : mvn dependency:tree > output.txt
2. Then export DependencyTreeFilePath=path to the output.txt file
3. Then export MavenRepoPath=path to the local maven repository
4. Then go to this current DependencyTreeParser directory and execute : mvn clean install (This command will display all the packages being used/loaded in the maven repo)
