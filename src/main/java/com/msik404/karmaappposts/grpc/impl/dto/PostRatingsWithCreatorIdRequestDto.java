package com.msik404.karmaappposts.grpc.impl.dto;

import com.msik404.karmaappposts.grpc.PostRatingsWithCreatorIdRequest;
import com.msik404.karmaappposts.grpc.impl.exception.UnsupportedVisibilityException;
import org.bson.types.ObjectId;

public record PostRatingsWithCreatorIdRequestDto(PostRatingsRequestDto postRatingsRequestDto, ObjectId creatorId) {

    public PostRatingsWithCreatorIdRequestDto(
            PostRatingsWithCreatorIdRequest request) throws UnsupportedVisibilityException {

        this(
                new PostRatingsRequestDto(request.getPostsRatingsRequest()),
                new ObjectId(request.getCreatorId())
        );
    }

}
