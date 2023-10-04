package com.msik404.karmaappposts.grpc.impl.dto;

import java.util.List;

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

public record PostsRequestDto(Integer size, PostDocScrollPositionConcrete position, List<Visibility> visibilities,
                              PostDocRetrievalOrderStrategy order) {

    public static PostDocScrollPositionConcrete map(@NonNull ScrollPosition position) {

        return PostDocScrollPosition.of(
                position.getKarmaScore(),
                new ObjectId(position.getPostId())
        );
    }

    public static PostDocRetrievalOrderStrategy map(boolean isDescending) {
        return isDescending ? PostDocRetrievalOrder.desc() : PostDocRetrievalOrder.asc();
    }

    public PostsRequestDto() {
        this(null, null, null, null);
    }

    public PostsRequestDto(PostsRequest request) throws UnsupportedVisibilityException {

        this(
                request.hasSize() ? request.getSize() : null,
                request.hasPosition() ? PostsRequestDto.map(request.getPosition()) : null,
                request.getVisibilitiesCount() != 0 ? request.getVisibilitiesList().stream()
                        .map(VisibilityMapper::map).toList() : null,
                request.hasIsDescending() ? PostsRequestDto.map(request.getIsDescending()) : null
        );
    }

}
