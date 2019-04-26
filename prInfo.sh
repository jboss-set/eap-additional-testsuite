#!/bin/bash

prs=$(curl -s -n https://api.github.com/repos/$REPO/issues/$PR | jq -r .body)
declare -A arr
i=0;
while read -r line
do

    if [[ $line == *"SPR"* ]]; then
        i=$((i+1));
        line=${line#*]}
        line=${line#*[}
        line=${line%]*}
	set -- "$line" 
	IFS=","; declare -a Array=($*)
	arr[$i,1]=${Array[0]//[[:space:]]/}
	arr[$i,2]=${Array[1]//[[:space:]]/}
        arr[$i,3]=${Array[2]//[[:space:]]/} 
	arr[$i,4]=${Array[3]//[[:space:]]/}
    fi
    
done <<< $prs

echo $i;
echo "${arr[1,1]} ${arr[1,2]} ${arr[1,3]} ${arr[1,4]}"
echo "${arr[2,1]} ${arr[2,2]} ${arr[2,3]} ${arr[2,4]}"

