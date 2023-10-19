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
            @NonNull ObjectId postId,
            @NonNull ObjectId userId) throws RatingNotFoundException {

        return ratingRepository.findByPostIdAndUserId(postId, userId).orElseThrow(RatingNotFoundException::new);
    }

    public void findAndSetIsPositiveById(
            @NonNull ObjectId postId,
            boolean isPositive) throws RatingNotFoundException {

        long affectedDocs = ratingRepository.findAndSetIsPositiveById(postId, isPositive);
        if (affectedDocs == 0) {
            throw new RatingNotFoundException();
        }
    }

    @NonNull
    public List<IdAndIsPositiveOnlyDto> findFirstN(
            @Nullable Integer size,
            @NonNull ObjectId clientId,
            @Nullable PostDocScrollPositionConcrete position,
            @Nullable Collection<Visibility> visibilities,
            @Nullable PostDocRetrievalOrderStrategy order) {

        var params = new FindParametersDto(size, position, visibilities, order);

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
            @Nullable Integer size,
            @NonNull ObjectId creatorId,
            @NonNull ObjectId clientId,
            @Nullable PostDocScrollPositionConcrete position,
            @Nullable Collection<Visibility> visibilities,
            @Nullable PostDocRetrievalOrderStrategy order) {

        var params = new FindParametersDto(size, position, visibilities, order);

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
