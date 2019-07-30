#!/bin/bash

#echo %PrNum%;
echo "##teamcity[setParameter name='ServerPrNum' value='null']";

#cd server
#ls -la
#cd dist/target
#ls -la
#cd ../../..

eatPr=%teamcity.build.branch%
mkdir -p eat
cd eat
git clone https://github.com/jboss-set/eap-additional-testsuite.git
cd eap-additional-testsuite

if [[ $eatPr == *"pull"* ]]; then
    eatPrNum=$(echo $eatPr | cut -d'/' -f2); 
    echo $eatPrNum

    set -e

    git fetch origin +refs/pull/$eatPrNum/merge;
    git checkout FETCH_HEAD;

    set +e

commits1=$(curl -s -n https://api.github.com/repos/jboss-set/eap-additional-testsuite/pulls?state=open);
echo $commits1
prs=$(echo $commits1 | grep -Po '"number":.*?[^\\]",');
commits=$(echo $commits1 | grep -Po '"statuses_url":.*?[^\\]",');
cc=$(echo "${commits}" | paste -sd ?);

pp=$(echo "${prs}" | paste -sd ?);

IFS='?' read -r -a array <<< "$cc";
IFS='?' read -r -a array2 <<< "$pp";
j=0;

for line in "${array[@]}"
do
    
    if [[ $line != *"sha"* ]]; then
    j=$((j+1)); 

    line=${line#*statuses/};

    line=$(echo $line | cut -c1-40);
    git show --name-only $line
    exists=$(git show --name-only $line);
    if [[ $exists == *"commit"* ]]; then
        k=0;
        for line2 in "${array2[@]}"
        do
            if [[ $line2 == *"number"* ]]; then
            k=$((k+1)); 
            if [[ $j == $k ]]; then
            line2=${line2#*: }
            IFS=',' read -r -a array3 <<< "$line2";
            PR=${array3[0]};

            if [[ $PR == $eatPrNum ]]; then
            
            sprs=$(curl -s -n https://api.github.com/repos/jboss-set/eap-additional-testsuite/issues/$PR);
            echo ;
            spr=$(echo $sprs | grep -Po '"body":.*?[^\\]",');

            spr2=$(echo $spr | cut -d'{' -f1); 
  
            declare -A arr;
            i=0;
            IFS=$';' read -r -a array4 <<< "$spr2";

            for line4 in "${array4[@]}"
            do
            if [[ $line4 == *"SPR"* ]]; then

                i=$((i+1));
                line4=${line4#*SPR}
                line4=${line4#*]}
                line4=${line4#*[}
                line4=${line4%]*}
	        set -- "$line4" 
	        IFS=","; declare -a Array=($*)
	        arr[$i,1]=${Array[0]//[[:space:]]/}
	        arr[$i,2]=${Array[1]//[[:space:]]/}
                arr[$i,3]=${Array[2]//[[:space:]]/} 
	        arr[$i,4]=${Array[3]//[[:space:]]/}
                repo=${arr[$i,2]#*:}
                branch=${arr[$i,3]#*:}
                prn=${arr[$i,4]#*:}
                echo $repo $branch $prn
                if [[ $repo == "wildfly" ]]; then
                    if [[ $branch == "master" ]]; then
                        merged=$(curl -s -n https://api.github.com/repos/wildfly/wildfly/pulls/$prn | grep \"merged\":);
                        if [[ $merged != *"true"* ]]; then
                            echo "##teamcity[setParameter name='ServerPrNum' value='$prn']";
                        fi
                    fi
                fi
              
            fi

            done 
         fi
         fi
         fi
        done
    fi
    fi
done

fi
