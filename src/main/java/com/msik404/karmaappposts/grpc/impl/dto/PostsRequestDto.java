package com.msik404.karmaappposts.grpc.impl.dto;

import java.util.Collection;

import com.msik404.karmaappposts.grpc.PostsRequest;
import com.msik404.karmaappposts.grpc.ScrollPosition;
import com.msik404.karmaappposts.grpc.impl.exception.UnsupportedVisibilityException;
import com.msik404.karmaappposts.grpc.impl.mapper.VisibilityMapper;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrder;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPosition;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record PostsRequestDto(@Nullable Integer size, @Nullable PostDocScrollPositionConcrete position,
                              @Nullable Collection<Visibility> visibilities,
                              @Nullable PostDocRetrievalOrderStrategy order) {

    @NonNull
    public static PostDocScrollPositionConcrete map(@NonNull ScrollPosition position) {

        return PostDocScrollPosition.of(
                position.getKarmaScore(),
                new ObjectId(position.getPostId().getHexString())
        );
    }

    @NonNull
    public static PostDocRetrievalOrderStrategy map(boolean isDescending) {
        return isDescending ? PostDocRetrievalOrder.desc() : PostDocRetrievalOrder.asc();
    }

    public PostsRequestDto() {
        this(null, null, null, null);
    }

    public PostsRequestDto(@NonNull PostsRequest request) throws UnsupportedVisibilityException {

        this(
                request.hasSize() ? request.getSize() : null,
                request.hasPosition() ? PostsRequestDto.map(request.getPosition()) : null,
                request.getVisibilitiesCount() != 0 ? request.getVisibilitiesList().stream()
                        .map(VisibilityMapper::map).toList() : null,
                request.hasIsDescending() ? PostsRequestDto.map(request.getIsDescending()) : null
        );
    }

}
