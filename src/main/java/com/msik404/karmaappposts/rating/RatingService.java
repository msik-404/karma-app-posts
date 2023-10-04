package com.msik404.karmaappposts.rating;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.dto.FindParametersDto;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
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
                params.size(),
                clientId,
                params.position(),
                params.visibilities(),
                params.order()
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
                params.size(),
                creatorId,
                clientId,
                params.position(),
                params.visibilities(),
                params.order()
        );
    }

}
