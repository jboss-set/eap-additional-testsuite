#!/bin/sh

cd ./Dockerfiles/wildfly-chain
docker build -t docker.io/eat . > outputWildflyChain.txt

#sudo docker stop $(sudo docker ps -a -q)
#sudo docker rm $(sudo docker ps -a -q)
sudo docker rmi -f docker.io/eat


