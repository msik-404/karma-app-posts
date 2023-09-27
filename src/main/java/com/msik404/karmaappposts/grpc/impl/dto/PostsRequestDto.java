package com.msik404.karmaappposts.grpc.impl.dto;

import java.util.List;

import com.msik404.karmaappposts.grpc.PostsRequest;
import com.msik404.karmaappposts.grpc.ScrollPosition;
import com.msik404.karmaappposts.grpc.impl.mapper.VisibilityMapper;
import com.msik404.karmaappposts.grpc.impl.mapper.exception.UnsupportedVisibilityException;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrder;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPosition;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPositionConcrete;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class PostsRequestDto {

    private final int size;
    private final PostDocScrollPositionConcrete position;
    private final List<Visibility> visibilities;
    private final PostDocRetrievalOrderStrategy order;

    public static PostDocScrollPositionConcrete map(ScrollPosition position) {

        return PostDocScrollPosition.of(
                position.getKarmaScore(),
                new ObjectId(position.getPostId())
        );
    }

    public static PostDocRetrievalOrderStrategy map(boolean isDescending) {
        return isDescending ? PostDocRetrievalOrder.desc() : PostDocRetrievalOrder.asc();
    }

    public PostsRequestDto(PostsRequest request) throws UnsupportedVisibilityException {

        this.size = request.getSize();
        this.position = PostsRequestDto.map(request.getPosition());
        this.visibilities = request.getVisibilitiesList().stream().map(VisibilityMapper::map).toList();
        this.order = PostsRequestDto.map(request.getIsDescending());
    }

}
