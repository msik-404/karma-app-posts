package com.msik404.karmaappposts.post.repository;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPositionConcrete;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface CustomPostRepository {

    List<PostDocument> findFirstN(
            int size,
            @Nullable ObjectId creatorId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order
    );

}
