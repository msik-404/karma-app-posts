#! /usr/bin/bash

docker rm karma-app-posts-backend-1 && docker image rm karma-app-posts-backend
docker rm karma-app-posts-mongo-express-1 && docker rm karma-app-posts-mongo-1
docker volume rm karma-app-posts_db-config && docker volume rm karma-app-posts_db-data
docker network rm karma-app-posts_karma-app-net