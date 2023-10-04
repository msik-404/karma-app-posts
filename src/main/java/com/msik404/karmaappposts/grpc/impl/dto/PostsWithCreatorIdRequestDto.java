package com.msik404.karmaappposts.grpc.impl.dto;

import com.msik404.karmaappposts.grpc.PostsWithCreatorIdRequest;
import com.msik404.karmaappposts.grpc.impl.exception.UnsupportedVisibilityException;
import org.bson.types.ObjectId;

public record PostsWithCreatorIdRequestDto(PostsRequestDto postsRequestDto, ObjectId creatorId) {

    public PostsWithCreatorIdRequestDto(PostsWithCreatorIdRequest request) throws UnsupportedVisibilityException {

        this(
                request.hasPostsRequest() ? new PostsRequestDto(request.getPostsRequest()) : new PostsRequestDto(),
                new ObjectId(request.getCreatorId())
        );
    }
}
