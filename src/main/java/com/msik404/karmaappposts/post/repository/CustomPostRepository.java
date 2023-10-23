package com.msik404.karmaappposts.post.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.mongodb.client.result.UpdateResult;
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

    @NonNull
    Optional<PostDocumentWithImageData> findDocumentWithImageDataById(@NonNull ObjectId postId);

    UpdateResult findAndSetVisibilityById(@NonNull ObjectId id, @NonNull Visibility visibility);

}
