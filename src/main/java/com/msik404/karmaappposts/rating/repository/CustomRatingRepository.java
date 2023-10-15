package com.msik404.karmaappposts.rating.repository;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public interface CustomRatingRepository {

    @NonNull
    List<IdAndIsPositiveOnlyDto> findFirstN(
            final int size,
            @NonNull final ObjectId clientId,
            @NonNull final PostDocScrollPositionConcrete position,
            @NonNull final Collection<Visibility> visibilities,
            @NonNull final PostDocRetrievalOrderStrategy order
    );

    @NonNull
    List<IdAndIsPositiveOnlyDto> findFirstN(
            final int size,
            @NonNull final ObjectId creatorId,
            @NonNull final ObjectId clientId,
            @NonNull final PostDocScrollPositionConcrete position,
            @NonNull final Collection<Visibility> visibilities,
            @NonNull final PostDocRetrievalOrderStrategy order
    );

}
