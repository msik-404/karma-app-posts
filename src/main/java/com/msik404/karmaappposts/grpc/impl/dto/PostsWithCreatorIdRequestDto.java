package com.msik404.karmaappposts.grpc.impl.dto;

import java.util.List;

import com.msik404.karmaappposts.grpc.PostsWithCreatorIdRequest;
import com.msik404.karmaappposts.grpc.impl.exception.UnsupportedVisibilityException;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class PostsWithCreatorIdRequestDto {

    private final PostsRequestDto postsRequestDto;
    private final ObjectId creatorId;

    public PostsWithCreatorIdRequestDto(PostsWithCreatorIdRequest request) throws UnsupportedVisibilityException {

        this.postsRequestDto = request.hasPostsRequest() ? new PostsRequestDto(request.getPostsRequest()) : new PostsRequestDto();
        this.creatorId = new ObjectId(request.getCreatorId());
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
