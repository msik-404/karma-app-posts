# karma-app-posts
Posts microservice for karma-app microservices version. karma-app-posts is grpc server with access to mongodb database.

Check out other karma-app microservices:
- [karma-app-gateway](https://github.com/msik-404/karma-app-gateway)
- [karma-app-users](https://github.com/msik-404/karma-app-users)

# Technologies used
- Java 21
- MongoDB
- Docker
- gRPC
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

Additionally, because of [mongo_object_id.proto](https://github.com/msik-404/karma-app-posts/blob/main/src/main/proto/mongo_object_id.proto) file being extracted to reduce code duplication across many microservices,
which would use this message, this file needs to be imported in the same manner as explained above. Just import path 
to [proto](https://github.com/msik-404/karma-app-posts/tree/main/src/main/proto) folder.

# Protocol Documentation
<a name="top"></a>

## Table of Contents

- [karma_app_posts.proto](#karma_app_posts-proto)
    - [ChangePostVisibilityRequest](#karmaappposts-ChangePostVisibilityRequest)
    - [ChangedRatingResponse](#karmaappposts-ChangedRatingResponse)
    - [CreatePostRequest](#karmaappposts-CreatePostRequest)
    - [ImageRequest](#karmaappposts-ImageRequest)
    - [ImageResponse](#karmaappposts-ImageResponse)
    - [Post](#karmaappposts-Post)
    - [PostRating](#karmaappposts-PostRating)
    - [PostRatingsRequest](#karmaappposts-PostRatingsRequest)
    - [PostRatingsResponse](#karmaappposts-PostRatingsResponse)
    - [PostRatingsWithCreatorIdRequest](#karmaappposts-PostRatingsWithCreatorIdRequest)
    - [PostVisibilityResponse](#karmaappposts-PostVisibilityResponse)
    - [PostWithImageData](#karmaappposts-PostWithImageData)
    - [PostsRequest](#karmaappposts-PostsRequest)
    - [PostsResponse](#karmaappposts-PostsResponse)
    - [PostsWithCreatorIdRequest](#karmaappposts-PostsWithCreatorIdRequest)
    - [RatePostRequest](#karmaappposts-RatePostRequest)
    - [ScrollPosition](#karmaappposts-ScrollPosition)
    - [UnratePostRequest](#karmaappposts-UnratePostRequest)

    - [PostVisibility](#karmaappposts-PostVisibility)

    - [Posts](#karmaappposts-Posts)

- [mongo_object_id.proto](#mongo_object_id-proto)
    - [ProtoObjectId](#protomongo-ProtoObjectId)

- [Scalar Value Types](#scalar-value-types)



<a name="karma_app_posts-proto"></a>
<p align="right"><a href="#top">Top</a></p>

## karma_app_posts.proto



<a name="karmaappposts-ChangePostVisibilityRequest"></a>

### ChangePostVisibilityRequest
Represents request for changing visibility state of a given post.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| post_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of the post. |
| visibility | [PostVisibility](#karmaappposts-PostVisibility) | optional | Required new visibility state. |






<a name="karmaappposts-ChangedRatingResponse"></a>

### ChangedRatingResponse
Represents updated score of a post.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| delta | [int32](#int32) | optional | Required updated value of karma Score. |






<a name="karmaappposts-CreatePostRequest"></a>

### CreatePostRequest
Represents request for creating new post.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| user_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of a user. |
| headline | [string](#string) | optional | Optional post headline. |
| text | [string](#string) | optional | Optional post text. |
| image_data | [bytes](#bytes) | optional | Optional image data. |






<a name="karmaappposts-ImageRequest"></a>

### ImageRequest
Represents request for image of a given post.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| post_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of the post. |






<a name="karmaappposts-ImageResponse"></a>

### ImageResponse
Represents image of a given post. If image does not exist, image_data field is not set.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| image_data | [bytes](#bytes) | optional | Optional image data of the given post. |






<a name="karmaappposts-Post"></a>

### Post
Represents post object.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| post_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of the post. |
| user_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required if of the user. |
| headline | [string](#string) | optional | optional headline of the post |
| text | [string](#string) | optional | optional text of the post |
| karma_score | [sint64](#sint64) | optional | Required score of the post |
| visibility | [PostVisibility](#karmaappposts-PostVisibility) | optional | Required visibility of the post |






<a name="karmaappposts-PostRating"></a>

### PostRating
Represents rating of a given post.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| post_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of the post. |
| is_positive | [bool](#bool) | optional | Optional boolean value indicating whether rating is positive. Empty value means that post was not rated by the client user. |






<a name="karmaappposts-PostRatingsRequest"></a>

### PostRatingsRequest
Represents request for post ratings of a given client user.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| posts_request | [PostsRequest](#karmaappposts-PostsRequest) | optional | Optional PostsRequest. If empty, default values are deduced. |
| client_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of the client user. |






<a name="karmaappposts-PostRatingsResponse"></a>

### PostRatingsResponse
Represents list of post ratings returned in a from conforming PostsRatingsRequest.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| post_ratings | [PostRating](#karmaappposts-PostRating) | repeated | List of post ratings. |






<a name="karmaappposts-PostRatingsWithCreatorIdRequest"></a>

### PostRatingsWithCreatorIdRequest
Represents request similar to PostRatingsRequest with additional constraint that each post must have been created
by a given creator user.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| posts_ratings_request | [PostRatingsRequest](#karmaappposts-PostRatingsRequest) | optional | Optional PostRatingsRequest. If empty, default values are deduced. |
| creator_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of a creator user |






<a name="karmaappposts-PostVisibilityResponse"></a>

### PostVisibilityResponse
Represents visibility of a given post.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| visibility | [PostVisibility](#karmaappposts-PostVisibility) | optional | Required visibility of the given post |






<a name="karmaappposts-PostWithImageData"></a>

### PostWithImageData
Represents post with image data.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| post | [Post](#karmaappposts-Post) | optional | Required post. |
| image_data | [bytes](#bytes) | optional | Optional image data. If empty image does not exist |






<a name="karmaappposts-PostsRequest"></a>

### PostsRequest
Represents request for fetching given amount of posts with requested visibility in requested order, starting from
a given position.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| size | [uint32](#uint32) | optional | Optional amount of the requested posts. Default is 100. |
| position | [ScrollPosition](#karmaappposts-ScrollPosition) | optional | Optional starting position. Default is - return first posts. |
| visibilities | [PostVisibility](#karmaappposts-PostVisibility) | repeated | Optional visibility of the requested posts. Default is ACTIVE. |
| is_descending | [bool](#bool) | optional | Optional order of the posts. Default is from the posts with highest score to the posts with lowest score. |






<a name="karmaappposts-PostsResponse"></a>

### PostsResponse
Represents list of posts returned in a from conforming PostsRequest.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| posts | [Post](#karmaappposts-Post) | repeated | List of posts. |






<a name="karmaappposts-PostsWithCreatorIdRequest"></a>

### PostsWithCreatorIdRequest
Represents request similar to PostsRequest with additional constraint that each post must have been created by a
given creator user.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| posts_request | [PostsRequest](#karmaappposts-PostsRequest) | optional | Optional PostsRequest. If empty, default values are deduced. |
| creator_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of the creator user. |






<a name="karmaappposts-RatePostRequest"></a>

### RatePostRequest
Represents request for rating existing post by a client user.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| post_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of the post. |
| user_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of the user. |
| is_positive | [bool](#bool) | optional | Required value indicating whether rating is positive. |






<a name="karmaappposts-ScrollPosition"></a>

### ScrollPosition
Represents object used for performing pagination. It encodes ending position of the previous post fetch.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| karma_score | [uint64](#uint64) | optional | Required karma score of the last post in the previous post fetch. |
| post_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required postId of the last post in the previous post fetch. |






<a name="karmaappposts-UnratePostRequest"></a>

### UnratePostRequest
Represents request for unrating of existing post by a client user.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| post_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of the post. |
| user_id | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Required id of the user. |








<a name="karmaappposts-PostVisibility"></a>

### PostVisibility
Represents all accepted visibility states of a post.

| Name | Number | Description |
| ---- | ------ | ----------- |
| VIS_ACTIVE | 0 | Active post. |
| VIS_HIDDEN | 1 | Hidden post. |
| VIS_DELETED | 2 | Deleted post. |







<a name="karmaappposts-Posts"></a>

### Posts
Service for fetching, creating and changing state of posts.

| Method Name | Request Type | Response Type | Description |
| ----------- | ------------ | ------------- | ------------|
| createPost | [CreatePostRequest](#karmaappposts-CreatePostRequest) | [.google.protobuf.Empty](#google-protobuf-Empty) | Used for post creation. New posts are active by default and have karma score of zero. |
| ratePost | [RatePostRequest](#karmaappposts-RatePostRequest) | [ChangedRatingResponse](#karmaappposts-ChangedRatingResponse) | Used for post rating. This operation is idempotent. Returns encoded PostNotFoundException, RatingNotFoundException on failure. |
| unratePost | [UnratePostRequest](#karmaappposts-UnratePostRequest) | [ChangedRatingResponse](#karmaappposts-ChangedRatingResponse) | Used for post unrating. This operation is idempotent. Returns encoded PostNotFoundException on failure. |
| changePostVisibility | [ChangePostVisibilityRequest](#karmaappposts-ChangePostVisibilityRequest) | [.google.protobuf.Empty](#google-protobuf-Empty) | Used for changing post visibility. This operation is idempotent. Returns encoded PostNotFoundException, UnsupportedVisibilityException on failure. |
| findPosts | [PostsRequest](#karmaappposts-PostsRequest) | [PostsResponse](#karmaappposts-PostsResponse) | Used for fetching key-set: (karmaScore, postId) paginated posts. returns encoded UnsupportedVisibilityException on failure. |
| findPostsWithCreatorId | [PostsWithCreatorIdRequest](#karmaappposts-PostsWithCreatorIdRequest) | [PostsResponse](#karmaappposts-PostsResponse) | Used for fetching key-set: (karmaScore, postId) paginated posts of a given creator user. Returns encoded UnsupportedVisibilityException on failure. |
| findImage | [.protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | [ImageResponse](#karmaappposts-ImageResponse) | Used for fetching image of a given post by postId. Encodes ImageNotFoundException on failure. |
| findPostRatings | [PostRatingsRequest](#karmaappposts-PostRatingsRequest) | [PostRatingsResponse](#karmaappposts-PostRatingsResponse) | Used for fetching key-set: (karmaScore, postId) paginated post ratings of a given client user. Ratings are returned in the same order as rpc findPosts. Returns encoded UnsupportedVisibilityException on failure. |
| findPostRatingsWithCreatorId | [PostRatingsWithCreatorIdRequest](#karmaappposts-PostRatingsWithCreatorIdRequest) | [PostRatingsResponse](#karmaappposts-PostRatingsResponse) | Used for fetching key-set: (karmaScore, postId) paginated ratings of a given client user of posts of a given creator user. Ratings are returned in the same order as rpc findPostsWithCreatorId. Returns encoded UnsupportedVisibilityException. |
| findPostCreatorId | [.protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | [.protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | Used for finding creator userId of a given post by postId. Returns encoded PostNotFoundException. |
| findPostWithImageData | [.protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | [PostWithImageData](#karmaappposts-PostWithImageData) | Used for fetching post with it&#39;s image (if present) by postId. Returns encoded PostNotFoundException. |
| findPostVisibility | [.protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | [PostVisibilityResponse](#karmaappposts-PostVisibilityResponse) | Used for finding visibility of a given post by postId. Returns encoded PostNotFoundException. |





<a name="mongo_object_id-proto"></a>
<p align="right"><a href="#top">Top</a></p>

## mongo_object_id.proto



<a name="protomongo-ProtoObjectId"></a>

### ProtoObjectId
Represents a unique document identifier in MongoDB.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| hexString | [string](#string) | optional | Required, unique string of exactly 24 characters. |















## Scalar Value Types

| .proto Type | Notes | C++ | Java | Python | Go | C# | PHP | Ruby |
| ----------- | ----- | --- | ---- | ------ | -- | -- | --- | ---- |
| <a name="double" /> double |  | double | double | float | float64 | double | float | Float |
| <a name="float" /> float |  | float | float | float | float32 | float | float | Float |
| <a name="int32" /> int32 | Uses variable-length encoding. Inefficient for encoding negative numbers – if your field is likely to have negative values, use sint32 instead. | int32 | int | int | int32 | int | integer | Bignum or Fixnum (as required) |
| <a name="int64" /> int64 | Uses variable-length encoding. Inefficient for encoding negative numbers – if your field is likely to have negative values, use sint64 instead. | int64 | long | int/long | int64 | long | integer/string | Bignum |
| <a name="uint32" /> uint32 | Uses variable-length encoding. | uint32 | int | int/long | uint32 | uint | integer | Bignum or Fixnum (as required) |
| <a name="uint64" /> uint64 | Uses variable-length encoding. | uint64 | long | int/long | uint64 | ulong | integer/string | Bignum or Fixnum (as required) |
| <a name="sint32" /> sint32 | Uses variable-length encoding. Signed int value. These more efficiently encode negative numbers than regular int32s. | int32 | int | int | int32 | int | integer | Bignum or Fixnum (as required) |
| <a name="sint64" /> sint64 | Uses variable-length encoding. Signed int value. These more efficiently encode negative numbers than regular int64s. | int64 | long | int/long | int64 | long | integer/string | Bignum |
| <a name="fixed32" /> fixed32 | Always four bytes. More efficient than uint32 if values are often greater than 2^28. | uint32 | int | int | uint32 | uint | integer | Bignum or Fixnum (as required) |
| <a name="fixed64" /> fixed64 | Always eight bytes. More efficient than uint64 if values are often greater than 2^56. | uint64 | long | int/long | uint64 | ulong | integer/string | Bignum |
| <a name="sfixed32" /> sfixed32 | Always four bytes. | int32 | int | int | int32 | int | integer | Bignum or Fixnum (as required) |
| <a name="sfixed64" /> sfixed64 | Always eight bytes. | int64 | long | int/long | int64 | long | integer/string | Bignum |
| <a name="bool" /> bool |  | bool | boolean | boolean | bool | bool | boolean | TrueClass/FalseClass |
| <a name="string" /> string | A string must always contain UTF-8 encoded or 7-bit ASCII text. | string | String | str/unicode | string | string | string | String (UTF-8) |
| <a name="bytes" /> bytes | May contain any arbitrary sequence of bytes. | string | ByteString | str | []byte | ByteString | string | String (ASCII-8BIT) |


# Exception encoding
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