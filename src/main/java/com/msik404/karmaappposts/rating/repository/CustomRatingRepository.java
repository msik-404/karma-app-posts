package com.msik404.karmaappposts.rating.repository;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPositionConcrete;
import com.msik404.karmaappposts.rating.dto.PostIdAndIsPositiveOnlyDto;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public interface CustomRatingRepository {

    List<PostIdAndIsPositiveOnlyDto> findFirstN(
            int size,
            @NonNull ObjectId clientId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order
    );

    List<PostIdAndIsPositiveOnlyDto> findFirstN(
            int size,
            @NonNull ObjectId creatorId,
            @NonNull ObjectId clientId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order
    );

}
