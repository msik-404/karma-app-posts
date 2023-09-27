package com.msik404.karmaappposts.grpc.impl.dto;

import java.util.List;

import com.msik404.karmaappposts.grpc.PostsWithCreatorIdRequest;
import com.msik404.karmaappposts.grpc.impl.mapper.exception.UnsupportedVisibilityException;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPositionConcrete;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class PostsWithCreatorIdRequestDto {

    private final PostsRequestDto postsRequestDto;
    private final ObjectId creatorId;

    public PostsWithCreatorIdRequestDto(PostsWithCreatorIdRequest request) throws UnsupportedVisibilityException {

        this.postsRequestDto = new PostsRequestDto(request.getPostsRequest());
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
