package com.msik404.karmaappposts.post.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.dto.PostDocumentWithImageData;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public interface CustomPostRepository {

    @NonNull
    List<PostDocument> findFirstN(
            final int size,
            @NonNull final PostDocScrollPositionConcrete position,
            @NonNull final Collection<Visibility> visibilities,
            @NonNull final PostDocRetrievalOrderStrategy order
    );

    @NonNull
    List<PostDocument> findFirstN(
            final int size,
            @NonNull final ObjectId creatorId,
            @NonNull final PostDocScrollPositionConcrete position,
            @NonNull final Collection<Visibility> visibilities,
            @NonNull final PostDocRetrievalOrderStrategy order
    );

    @NonNull
    Optional<PostDocumentWithImageData> findDocumentWithImageData(@NonNull final ObjectId postId);

}
