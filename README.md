# karma-app-posts
Posts microservice for karma-app microservices version. karma-app-posts is grpc server with access to mongodb database.

Check out other karma-app microservices:
- [karma-app-gateway](https://github.com/msik-404/karma-app-gateway)
- [karma-app-users](https://github.com/msik-404/karma-app-users)

# Technologies used
- Java 21
- MongoDB
- Docker
- Grpc
- Java spring
- [spring-boot-starter-data-mongodb](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- spring-boot-starter-test
- [spring-boot-testcontainers](https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1)
- junit-jupiter
- [grpc-java](https://github.com/grpc/grpc-java) 
- [protovalidate-java](https://github.com/bufbuild/protovalidate-java)
- lombok

# gRPC, Protobuf and protovalidate
[gRPC](https://grpc.io/) is a modern open source high performance Remote Procedure Call (RPC) framework that can run in 
any environment. gRPC simplifies microservices API implementation and later the usage of the API. gRPC is self-documenting,
all available service methods and message structures can be found inside [proto file](https://github.com/msik-404/karma-app-posts/blob/main/src/main/proto/karma_app_posts.proto).

In this project to help with message validation [protovalidate-java](https://github.com/bufbuild/protovalidate-java) is used.
This project significantly simplifies validation of messages and reduces the time required to build stable system.
Additionally potential user of this microservice can see which fields are required and what
constraints need to be met to build valid message.

## Using postman
To test this microservice in postman one must not only import .proto file in postman service definition but also import 
path to .proto files of protovalidate-java. Usually this path looks something like this: 
some_personal_path/karma-app-posts/target/protoc-dependencies/some-long-code. Under some-long-code there should be the 
following files buf/validate/priv/expression.proto and buf/validate/priv/validate.proto.

# Features

## Service methods
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
### rpc createPost(CreatePostRequest) returns (google.protobuf.Empty) {}
- Create post with optional image. The Image if present, will get compressed with jpeg for storing efficiency. 
Initially post has zero karma score and active visibility.

### rpc ratePost(RatePostRequest) returns (ChangedRatingResponse) {}
- Rate positively, negatively. Operation is idempotent if post was already rated positively by some client rating it 
positively again doesn't do anything.

### rpc unratePost(UnratePostRequest) returns (ChangedRatingResponse) {}
- Unrate posts. This operation is also idempotent.

### rpc changePostVisibility(ChangePostVisibilityRequest) returns (google.protobuf.Empty) {}
- Change visibility to Active, Hidden or Deleted. This operation is also idempotent Active->Active etc.

### rpc findPosts(PostsRequest) returns (PostsResponse) {}
- Get paginated posts. Posts are paginated using key-set pagination on tuple (karmaScore, userId) if two karmaScores
are the same, greater userId is first.

### rpc findPostsWithCreatorId(PostsWithCreatorIdRequest) returns (PostsResponse) {}
- Get paginated posts created by a given user.

### rpc findImage(ImageRequest) returns (ImageResponse) {}
- Get image of a given post.

### rpc findPostRatings(PostRatingsRequest) returns (PostRatingsResponse) {}
 - Get ratings of a given client user. Ratings are paginated and returned in the same order as posts. So client can easily
combine data from these two sources to present data to the frontend.

### rpc findPostRatingsWithCreatorId(PostRatingsWithCreatorIdRequest) returns (PostRatingsResponse) {}
- Get paginated ratings of some client user of posts created by given creator user.

### rpc findPostCreatorId(PostCreatorIdRequest) returns (PostCreatorIdResponse) {}
- Get user id by post id

### rpc findPostWithImageData(PostRequest) returns (PostWithImageData) {}
- Get post data with image by post id

### rpc findPostVisibility(MongoObjectId) returns (PostVisibilityResponse) {}
- Get post visibility by post id

To see message structure look inside [proto file](https://github.com/msik-404/karma-app-posts/blob/main/src/main/proto/karma_app_posts.proto).

## Exception encoding
When some exception which is not critical is thrown on the backend side, it is being encoded and passed with appropriate
gRPC code to the caller. Each exception has its unique identifier. With this it can be decoded on the caller side.
In this setup client side can use the same exception classes as backend.

Simple [encoding class](https://github.com/msik-404/karma-app-posts/blob/main/src/main/java/com/msik404/karmaappposts/encoding/ExceptionEncoder.java)
which simply inserts "exceptionId EXCEPTION_ID" at the begging of error message. This EXCEPTION_ID can be parsed with 
simple regex.

Each encodable exception must implement [EncodableException](https://github.com/msik-404/karma-app-posts/blob/main/src/main/java/com/msik404/karmaappposts/encoding/EncodableException.java)
and [GrpcStatusException](https://github.com/msik-404/karma-app-posts/blob/main/src/main/java/com/msik404/karmaappposts/grpc/impl/exception/GrpcStatusException.java).

# Environment variables
Backend requires four environment variables to be set:
- KARMA_APP_POSTS_DB_HOST
- KARMA_APP_POSTS_DB_NAME
- KARMA_APP_POSTS_DB_USER
- KARMA_APP_POSTS_DB_PASSWORD

for details see: [application.yaml](https://github.com/msik-404/karma-app-posts/blob/main/src/main/resources/application.yaml).

Additionally, [docker-compose.yaml](https://github.com/msik-404/karma-app-posts/blob/main/docker-compose.yaml) needs:
- KARMA_APP_POSTS_HOST

Simply create .env and place it in the root of project.

For example:
```
KARMA_APP_POSTS_DB_HOST=posts-db
KARMA_APP_POSTS_DB_NAME=posts-db
KARMA_APP_POSTS_DB_USER=dev
KARMA_APP_POSTS_DB_PASSWORD=dev
KARMA_APP_POSTS_HOST=karma-app-posts
```

# Building the project
To get target folder and build the project with maven simply run: 
```
./mvnw clean package -DskipTests
```

If one would like to build the project with running the tests, one must have docker installed on their machine and run:
```
./mvnw clean package
```

# Tests
Docker is required to run tests locally because [Testcontainers for Java](https://java.testcontainers.org/) is used for
integration tests.

All the code which comes into contact with data persistence is tested in integration tests under 
[src/test](https://github.com/msik-404/karma-app-posts/tree/main/src/test).
The rest of the code is much simpler and easier to follow and was tested manually using postman.

# Transaction requirements
Because backend of this microservice uses transactions, mongodb cannot be run in standalone server mode. It needs
either replica-set or cluster. In this case single node replica-set is used. Inside  [mongo health-check](https://github.com/msik-404/karma-app-posts/blob/main/docker-compose.yaml#L33)
there is a simple script which checks for replica-set status. If status indicates that replica-set is not initiated,
initiation happens. If initiation is successful, 1 is returned and container is healthy, else container is unhealthy.
Other containers wait for mongo container to become healthy.

Mongo replica-set authentication minimally requires `keyfile`.
To generate one simply run:
```
openssl rand -base64 756 > keyfile
chmod 600 keyfile
```

# Starting the microservice | deployment for testing

To start the microservice locally, docker compose is required.

In this repository one can find [docker-compose-yaml](https://github.com/msik-404/karma-app-posts/blob/main/docker-compose.yaml).

To start all containers one should run in the root of the project:
```
docker compose up
```
To stop containers:
```
docker compose stop
```
To remove containers and their data:
```
docker compose down -v
```