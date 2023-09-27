package com.msik404.karmaappposts.grpc.impl;

import java.util.List;

import com.msik404.karmaappposts.grpc.impl.dto.PostRatingsRequestDto;
import com.msik404.karmaappposts.grpc.impl.dto.PostRatingsWithCreatorIdRequestDto;
import com.msik404.karmaappposts.rating.dto.PostIdAndIsPositiveOnlyDto;
import com.msik404.karmaappposts.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingRepositoryGrpcHandler {

    RatingRepository ratingRepository;

    List<PostIdAndIsPositiveOnlyDto> findFirstN(PostRatingsRequestDto request) {

        return ratingRepository.findFirstN(
                request.getSize(),
                request.getClientId(),
                request.getPosition(),
                request.getVisibilities(),
                request.getOrder()
        );
    }

    List<PostIdAndIsPositiveOnlyDto> findFirstN(PostRatingsWithCreatorIdRequestDto request) {

        return ratingRepository.findFirstN(
                request.getSize(),
                request.getCreatorId(),
                request.getClientId(),
                request.getPosition(),
                request.getVisibilities(),
                request.getOrder()
        );
    }

}