package com.msik404.karmaappposts.post.repository;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public interface CustomPostRepository {

    @NonNull
    List<PostDocument> findFirstN(
            int size,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order
    );

    @NonNull
    List<PostDocument> findFirstN(
            int size,
            @NonNull ObjectId creatorId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order
    );

}
