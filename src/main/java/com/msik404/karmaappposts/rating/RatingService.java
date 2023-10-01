package com.msik404.karmaappposts.rating;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.dto.FindParametersDto;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPositionConcrete;
import com.msik404.karmaappposts.rating.dto.PostIdAndIsPositiveOnlyDto;
import com.msik404.karmaappposts.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public List<PostIdAndIsPositiveOnlyDto> findFirstN(
            @Nullable Integer size,
            @NonNull ObjectId clientId,
            @Nullable PostDocScrollPositionConcrete position,
            @Nullable Collection<Visibility> visibilities,
            @Nullable PostDocRetrievalOrderStrategy order) {

        final var params = new FindParametersDto(size, position, visibilities, order);

        return ratingRepository.findFirstN(
                params.getSize(),
                clientId,
                params.getPosition(),
                params.getVisibilities(),
                params.getOrder()
        );
    }

    public List<PostIdAndIsPositiveOnlyDto> findFirstN(
            @Nullable Integer size,
            @NonNull ObjectId creatorId,
            @NonNull ObjectId clientId,
            @Nullable PostDocScrollPositionConcrete position,
            @Nullable Collection<Visibility> visibilities,
            @Nullable PostDocRetrievalOrderStrategy order) {

        final var params = new FindParametersDto(size, position, visibilities, order);

        return ratingRepository.findFirstN(
                params.getSize(),
                creatorId,
                clientId,
                params.getPosition(),
                params.getVisibilities(),
                params.getOrder()
        );
    }

}
