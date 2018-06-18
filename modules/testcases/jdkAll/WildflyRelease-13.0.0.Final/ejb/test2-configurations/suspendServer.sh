PPIDD=$(ps -aux | grep "standalone.sh" | grep "bin" | awk '{print $2}')
echo $PPIDD
for childprocess in $(ps -o pid --ppid $PPIDD | tail -n +2); 
    do kill -TSTP $childprocess; 
    echo $childprocess;
done;
#kill -TSTP $(ps -aux | grep "standalone.sh" | grep "bin" | awk '{print $2}')

