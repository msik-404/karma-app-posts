# Protocol Documentation
<a name="top"></a>

## Table of Contents

- [karma_app_posts.proto](#karma_app_posts-proto)
    - [ChangePostVisibilityRequest](#karmaappposts-ChangePostVisibilityRequest)
    - [ChangedRatingResponse](#karmaappposts-ChangedRatingResponse)
    - [CreatePostRequest](#karmaappposts-CreatePostRequest)
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

