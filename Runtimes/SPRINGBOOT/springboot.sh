#!/bin/bash
rm -R SpringBootAT
rm -R SpringBoot1
rm -R SpringBoot2

git clone https://github.com/panossot/SpringBootAT.git

## Here we add the remote testsuites##
git clone https://github.com/panossot/remoteReposAT.git SpringBoot1  --branch SpringBoot1
cp -R ./SpringBoot1/org ./SpringBootAT/modules/src/test/java/present
git clone https://github.com/panossot/remoteReposAT.git SpringBoot2  --branch SpringBoot2
cp -R ./SpringBoot2/org ./SpringBootAT/modules/src/test/java/present
######################################

cd SpringBootAT
mvn clean install -Dmaster > ../output.txt

echo "$(cat ../output.txt)"

if [ -f "../errors.txt" ]
then
  rm ../errors.txt
fi

if [[ $(cat ../output.txt) == *"ERROR"* ]]
then
    echo  >> ../errors.txt
    (echo "Spring Boot Additional Testsuite was completed with errors ...") >> ../errors.txt
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
    echo "Spring Boot Additional Testsuite was completed with errors ..."
else
    echo "Spring Boot Additional Testsuite was completed successfully ..."
fi
