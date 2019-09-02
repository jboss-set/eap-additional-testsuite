#!/bin/sh

cd ./Dockerfiles/JBossServers72x-chain
docker build -t docker.io/eat72x . > outputEap72xChain.txt

#sudo docker stop $(sudo docker ps -a -q)
#sudo docker rm $(sudo docker ps -a -q)
sudo docker rmi -f docker.io/eat72x
