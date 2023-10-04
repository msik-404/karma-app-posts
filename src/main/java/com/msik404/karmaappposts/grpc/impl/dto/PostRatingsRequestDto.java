package com.msik404.karmaappposts.grpc.impl.dto;

import com.msik404.karmaappposts.grpc.PostRatingsRequest;
import com.msik404.karmaappposts.grpc.impl.exception.UnsupportedVisibilityException;
import org.bson.types.ObjectId;

public record PostRatingsRequestDto(PostsRequestDto postsRequestDto, ObjectId clientId) {

    public PostRatingsRequestDto(PostRatingsRequest request) throws UnsupportedVisibilityException {

        this(
                request.hasPostsRequest() ? new PostsRequestDto(request.getPostsRequest()) : new PostsRequestDto(),
                new ObjectId(request.getClientId())
        );
    }
}
