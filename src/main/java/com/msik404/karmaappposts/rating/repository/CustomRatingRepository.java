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

    List<IdAndIsPositiveOnlyDto> findFirstN(
            int size,
            @NonNull ObjectId clientId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order
    );

    List<IdAndIsPositiveOnlyDto> findFirstN(
            int size,
            @NonNull ObjectId creatorId,
            @NonNull ObjectId clientId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order
    );

}
