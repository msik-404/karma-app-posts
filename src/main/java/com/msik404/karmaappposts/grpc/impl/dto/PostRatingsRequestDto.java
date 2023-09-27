package com.msik404.karmaappposts.grpc.impl.dto;

import java.util.List;

import com.msik404.karmaappposts.grpc.PostRatingsRequest;
import com.msik404.karmaappposts.grpc.impl.mapper.exception.UnsupportedVisibilityException;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPositionConcrete;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class PostRatingsRequestDto {

    private final PostsRequestDto postsRequestDto;
    private final ObjectId clientId;

    public PostRatingsRequestDto(PostRatingsRequest request) throws UnsupportedVisibilityException {

        this.postsRequestDto = new PostsRequestDto(request.getPostsRequest());
        this.clientId = new ObjectId(request.getClientId());
    }

    public int getSize() {
        return postsRequestDto.getSize();
    }

    public PostDocScrollPositionConcrete getPosition() {
        return postsRequestDto.getPosition();
    }

    public List<Visibility> getVisibilities() {
        return postsRequestDto.getVisibilities();
    }

    public PostDocRetrievalOrderStrategy getOrder() {
        return postsRequestDto.getOrder();
    }

}
