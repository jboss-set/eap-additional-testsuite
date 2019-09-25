#!/bin/bash
rm -Rf ThorntailAT
rm -Rf Thorntail1
rm -Rf Thorntail2
rm -Rf Thorntail3
rm -Rf RemoteTests

git clone https://github.com/panossot/ThorntailAT.git

## Here we add the remote testsuites##
git clone https://github.com/panossot/remoteReposAT.git Thorntail1  --branch Thorntail1
cp -R ./Thorntail1/org ./ThorntailAT/modules/src/test/java/present
git clone https://github.com/panossot/remoteReposAT.git Thorntail2  --branch Thorntail2
cp -R ./Thorntail2/org ./ThorntailAT/modules/src/test/java/present
#git clone https://github.com/panossot/remoteReposAT.git Thorntail3  --branch Thorntail3
#cp -R ./Thorntail3/src/test/java/org ./ThorntailAT/modules/src/test/java/present
git clone https://github.com/panossot/remoteReposAT.git RemoteTests  --branch master
cp -R ./RemoteTests/src/test/java/org ./ThorntailAT/modules/src/test/java/present
######################################

cd ThorntailAT
mvn clean install -Dmaster > ../output.txt

echo "$(cat ../output.txt)"

if [ -f "../errors.txt" ]
then
  rm ../errors.txt
fi

if [[ $(cat ../output.txt) == *"ERROR"* ]]
then
    echo  >> ../errors.txt
    (echo "Thorntail Additional Testsuite was completed with errors ...") >> ../errors.txt
    echo  >> ../errors.txt
    (echo "BUILD ERRORS ...") >> ../errors.txt
    (grep 'ERROR' ../output.txt) >> ../errors.txt
    echo  >> ../errors.txt
    i=0
    while read line
    do 
        [ -z "$line" ] && break
        if [ $i -eq 0 ]
	then
	  (echo "TEST ERRORS ...") >> ../errors.txt
	fi
        echo "$line" >> ../errors.txt
        i=1
    done < <(grep -A10 'Tests run.*Failures.*Errors.* Skipped.*Time elapsed.*FAILURE' ../output.txt)
    echo  >> ../errors.txt
    echo "Thorntail Additional Testsuite was completed with errors ..."
else
    echo "Thorntail Additional Testsuite was completed successfully ..."
fi
