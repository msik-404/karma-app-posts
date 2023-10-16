package com.msik404.karmaappposts.rating;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.dto.FindParametersDto;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
import com.msik404.karmaappposts.rating.exception.RatingNotFoundException;
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

    @NonNull
    public IdAndIsPositiveOnlyDto findByPostIdAndUserId(
            @NonNull final ObjectId postId,
            @NonNull final ObjectId userId) throws RatingNotFoundException {

        return ratingRepository.findByPostIdAndUserId(postId, userId).orElseThrow(RatingNotFoundException::new);
    }

    public void findAndSetIsPositiveById(
            @NonNull final ObjectId postId,
            final boolean isPositive) throws RatingNotFoundException {

        final long affectedDocs = ratingRepository.findAndSetIsPositiveById(postId, isPositive);
        if (affectedDocs == 0) {
            throw new RatingNotFoundException();
        }
    }

    @NonNull
    public List<IdAndIsPositiveOnlyDto> findFirstN(
            @Nullable final Integer size,
            @NonNull final ObjectId clientId,
            @Nullable final PostDocScrollPositionConcrete position,
            @Nullable final Collection<Visibility> visibilities,
            @Nullable final PostDocRetrievalOrderStrategy order) {

        final var params = new FindParametersDto(size, position, visibilities, order);

        return ratingRepository.findFirstN(
                params.size(),
                clientId,
                params.position(),
                params.visibilities(),
                params.order()
        );
    }

    @NonNull
    public List<IdAndIsPositiveOnlyDto> findFirstN(
            @Nullable final Integer size,
            @NonNull final ObjectId creatorId,
            @NonNull final ObjectId clientId,
            @Nullable final PostDocScrollPositionConcrete position,
            @Nullable final Collection<Visibility> visibilities,
            @Nullable final PostDocRetrievalOrderStrategy order) {

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
