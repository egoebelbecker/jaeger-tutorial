#!/bin/sh

usage()
 {
 echo "Usage: $0 release"
 exit 1
 }

if [ $# -ne 1 ] ; then
    usage
else
    export release=$1
fi

echo "Build and Push jaegertutorial:"${release}" to Docker repo"

docker build -t jaegertutorial:${release} .
docker tag jaegertutorial:${release} ericgoebelbecker/jaegertutorial:${release}
docker push ericgoebelbecker/jaegertutorial:${release}

