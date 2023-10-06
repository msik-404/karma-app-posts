package com.msik404.karmaappposts.grpc.impl;

import java.util.List;

import com.msik404.karmaappposts.grpc.impl.dto.PostRatingsRequestDto;
import com.msik404.karmaappposts.grpc.impl.dto.PostRatingsWithCreatorIdRequestDto;
import com.msik404.karmaappposts.rating.RatingService;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingRepositoryGrpcHandler {

    RatingService ratingService;

    @NonNull
    List<IdAndIsPositiveOnlyDto> findFirstN(@NonNull PostRatingsRequestDto request) {

        return ratingService.findFirstN(
                request.postsRequestDto().size(),
                request.clientId(),
                request.postsRequestDto().position(),
                request.postsRequestDto().visibilities(),
                request.postsRequestDto().order()
        );
    }

    @NonNull
    List<IdAndIsPositiveOnlyDto> findFirstN(@NonNull PostRatingsWithCreatorIdRequestDto request) {

        return ratingService.findFirstN(
                request.postRatingsRequestDto().postsRequestDto().size(),
                request.creatorId(),
                request.postRatingsRequestDto().clientId(),
                request.postRatingsRequestDto().postsRequestDto().position(),
                request.postRatingsRequestDto().postsRequestDto().visibilities(),
                request.postRatingsRequestDto().postsRequestDto().order()
        );
    }

}
