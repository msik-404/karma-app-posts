package com.msik404.karmaappposts.grpc.impl.dto;

import com.msik404.karmaappposts.grpc.PostRatingsRequest;
import com.msik404.karmaappposts.grpc.impl.exception.UnsupportedVisibilityException;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public record PostRatingsRequestDto(@NonNull PostsRequestDto postsRequestDto, @NonNull ObjectId clientId) {

    public PostRatingsRequestDto(@NonNull PostRatingsRequest request) throws UnsupportedVisibilityException {

        this(
                request.hasPostsRequest() ? new PostsRequestDto(request.getPostsRequest()) : new PostsRequestDto(),
                new ObjectId(request.getClientId().getHexString())
        );
    }
}
