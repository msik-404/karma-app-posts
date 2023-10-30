# karma-app-posts
Posts microservice for karma-app microservices version. karma-app-posts is grpc server with access to mongodb database.

# Technologies used
- Java spring
- spring-boot-starter-data-mongodb
- spring-boot-starter-test
- spring-boot-testcontainers
- junit-jupiter
- [grpc-java](https://github.com/grpc/grpc-java) 
- [protovalidate-java](https://github.com/bufbuild/protovalidate-java)
- lombok

# Grpc, Protobuf and protovalidate
Thanks to the use of grpc all available service methods and messages can be 
inspected inside [proto file](https://github.com/msik-404/karma-app-posts/blob/main/src/main/proto/karma_app_posts.proto).

In this project to help with message validation I use: [protovalidate-java](https://github.com/bufbuild/protovalidate-java).
This project significantly simplifies validation of messages and reduces the time required to build stable system.
Additionally potential user of this microservice can see which fields are required and what
constraints need to be met to build valid message.

### Using postman
To test this microservice in postman one must not only import .proto file in postman service definition but also import 
path to .proto files of protovalidate-java. Usually this path looks something like this: 
some_personal_path/karma-app-posts/target/protoc-dependencies/some-long-code. Under some-long-code there should be the 
following files buf/validate/priv/expression.proto and buf/validate/priv/validate.proto.

# Features
These are all the supported methods.

```
service Posts {

  rpc createPost(CreatePostRequest) returns (google.protobuf.Empty) {}
  rpc ratePost(RatePostRequest) returns (ChangedRatingResponse) {}
  rpc unratePost(UnratePostRequest) returns (ChangedRatingResponse) {}
  rpc changePostVisibility(ChangePostVisibilityRequest) returns (google.protobuf.Empty) {}
  rpc findPosts(PostsRequest) returns (PostsResponse) {}
  rpc findPostsWithCreatorId(PostsWithCreatorIdRequest) returns (PostsResponse) {}
  rpc findImage(ImageRequest) returns (ImageResponse) {}
  rpc findPostRatings(PostRatingsRequest) returns (PostRatingsResponse) {}
  rpc findPostRatingsWithCreatorId(PostRatingsWithCreatorIdRequest) returns (PostRatingsResponse) {}
  rpc findPostCreatorId(PostCreatorIdRequest) returns (PostCreatorIdResponse) {}
  rpc findPostWithImageData(PostRequest) returns (PostWithImageData) {}
  rpc findPostVisibility(MongoObjectId) returns (PostVisibilityResponse) {}

}
```
Running from top to bottom:
- Creating post with optional image. The Image if present, will get compressed with jpeg for storing efficiency. 
Initially post has zero karma score and active visibility.
- Rating positively, negatively. Operation is idempotent if post was already rated positively by some client rating it 
positively again doesn't do anything.
- Unrating posts. This operation is also idempotent.
- Changing visibility to Active, Hidden or Deleted. This operation is also idempotent Active->Active etc.
- Getting paginated posts. Posts are paginated using key-set pagination on tuple (karmaScore, userId) if two karmaScores
are the same, greater userId is first.
- Getting paginated posts created by a given user.
- Get image of a given post.
- Get ratings of a given client user. Ratings are paginated and returned in the same order as posts. So client can easily
combine data from these two sources to present data to the frontend.
- Getting paginated ratings of some client user of posts created by given creator user.
- Get user id by post id
- Get post data with image by post id
- Get post visibility by post id

To see message structure look inside [proto file](https://github.com/msik-404/karma-app-posts/blob/main/src/main/proto/karma_app_posts.proto).

# Bulding the project
To get target folder and build the project with maven simply run: 
```
./mvnw clean package -DskipTests
```

If one would like to build the project with running the tests, one must have docker installed on their machine and run:
```
./mvnw clean package
```

# Tests
Docker is required to run tests locally because I use [Testcontainers for Java](https://java.testcontainers.org/). 

All the code which comes into contact with data persistence is tested in integration tests under 
[src/test](https://github.com/msik-404/karma-app-posts/tree/main/src/test).
The rest of the code is much simpler and easier to follow and was tested manually using postman.

# Starting the microservice | deployment for testing
In this repository one can find [docker-compose-yaml](https://github.com/msik-404/karma-app-posts/blob/main/docker-compose.yaml)

To start the microservice one should use provided bash scripts but only docker can also be used.

### Bash scripts
Bash scripts can be found under scripts/ folder. 

Starting microservice: [start.sh](https://github.com/msik-404/karma-app-posts/blob/main/scripts/start.sh), 

Stopping microservice: [stop.sh](https://github.com/msik-404/karma-app-posts/blob/main/scripts/stop.sh) 

Cleaning after microservice: [clean.sh](https://github.com/msik-404/karma-app-posts/blob/main/scripts/clean.sh).

To run the scripts make them executable for example:
```
sudo chmod 744 *.sh
```
and then use:
```
./start.sh
./stop.sh
./clean.sh
```

### Pure docker method
```
docker compose up
# when all containers are runing, run the following commands in the seperate command line
docker exec -it karma-app-posts-mongo-1 mongosh --eval "rs.initiate()"
docker restart karma-app-posts-mongo-express-1
``` 

# Transaction requirements
Because backend of this microservice uses transactions, mongodb cannot be run in standalone server mode. It needs 
either replica-set or cluster. In this case I use single node replica-set. Script [mongo_rs_initiate.sh](https://github.com/msik-404/karma-app-posts/blob/main/scripts/mongo_rs_initiate.sh)
is used to initiate replica-set after starting mongodb container. Sadly I could not find a reliable way to do this initiation
inside docker-compose.yaml file, so this need to be made semi-manually with bash script. Also because of this initiation
[mongo-express](https://github.com/mongo-express/mongo-express) might fail, because of this [reset_mongo_express.sh](https://github.com/msik-404/karma-app-posts/blob/main/scripts/reset_mongo_express.sh) script exists. 
All of this is run inside [start.sh](https://github.com/msik-404/karma-app-posts/blob/main/scripts/start.sh). After 5 
seconds replica-set is being initiated and mongo-express container is being restarted. Of course this might fail because
of long running ```docker compose up``` when downloading images. To solve this issue after finishing dowloading images,
simply run ```./clean.sh``` and again ```./start.sh```.