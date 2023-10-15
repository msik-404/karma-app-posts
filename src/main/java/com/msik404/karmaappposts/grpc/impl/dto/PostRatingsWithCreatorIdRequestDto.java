package com.msik404.karmaappposts.grpc.impl.dto;

import com.msik404.karmaappposts.grpc.PostRatingsWithCreatorIdRequest;
import com.msik404.karmaappposts.grpc.impl.exception.UnsupportedVisibilityException;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public record PostRatingsWithCreatorIdRequestDto(@NonNull PostRatingsRequestDto postRatingsRequestDto,
                                                 @NonNull ObjectId creatorId) {

    public PostRatingsWithCreatorIdRequestDto(
            @NonNull final PostRatingsWithCreatorIdRequest request) throws UnsupportedVisibilityException {

        this(
                new PostRatingsRequestDto(request.getPostsRatingsRequest()),
                new ObjectId(request.getCreatorId().getHexString())
        );
    }

}
