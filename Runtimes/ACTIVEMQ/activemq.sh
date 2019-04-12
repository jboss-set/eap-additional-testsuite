#!/bin/bash
rm -R ActivemqAT
rm -R ActiveMQ1
rm -R ActiveMQ2

git clone https://github.com/panossot/ActivemqAT.git

## Here we add the remote testsuites##
git clone https://github.com/panossot/remoteReposAT.git ActiveMQ1  --branch ActiveMQ1
cp -R ./ActiveMQ1/org ./ActivemqAT/modules/src/test/java/present
git clone https://github.com/panossot/remoteReposAT.git ActiveMQ2  --branch ActiveMQ2
cp -R ./ActiveMQ2/org ./ActivemqAT/modules/src/test/java/present
######################################

cd ActivemqAT
mvn clean install -Dmaster > ../output.txt

echo "$(cat ../output.txt)"

if [ -f "../errors.txt" ]
then
  rm ../errors.txt
fi

if [[ $(cat ../output.txt) == *"ERROR"* ]]
then
    echo  >> ../errors.txt
    (echo "Active MQ Additional Testsuite was completed with errors ...") >> ../errors.txt
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
    echo "Active MQ Additional Testsuite was completed with errors ..."
else
    echo "Active MQ Additional Testsuite was completed successfully ..."
fi
