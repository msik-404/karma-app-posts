#! /usr/bin/bash

docker exec -it karma-app-posts-mongo-1 mongosh --eval "rs.initiate()"