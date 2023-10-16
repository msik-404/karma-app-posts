#! /usr/bin/bash

# stop containers
DIR=$(dirname $BASH_SOURCE)
source "$DIR/stop.sh"

# delete containers
docker rm karma-app-posts-backend-1 && docker rm karma-app-posts-mongo-express-1 && docker rm karma-app-posts-mongo-1

# deleted backend image
docker image rm karma-app-posts-backend

# delete volumes
docker volume rm karma-app-posts_db-config && docker volume rm karma-app-posts_db-data

# delete network
docker network rm karma-app-net