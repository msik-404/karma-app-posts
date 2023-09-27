package com.msik404.karmaappposts.grpc.impl.dto;

import java.util.List;

import com.msik404.karmaappposts.grpc.PostRatingsWithCreatorIdRequest;
import com.msik404.karmaappposts.grpc.impl.mapper.exception.UnsupportedVisibilityException;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPositionConcrete;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class PostRatingsWithCreatorIdRequestDto {

    private final PostRatingsRequestDto postRatingsRequestDto;
    private final ObjectId creatorId;

    public PostRatingsWithCreatorIdRequestDto(
            PostRatingsWithCreatorIdRequest request) throws UnsupportedVisibilityException {

        this.postRatingsRequestDto = new PostRatingsRequestDto(request.getPostsRatingsRequest());
        this.creatorId = new ObjectId(request.getCreatorId());
    }

    public int getSize() {
        return postRatingsRequestDto.getSize();
    }

    public PostDocScrollPositionConcrete getPosition() {
        return postRatingsRequestDto.getPosition();
    }

    public List<Visibility> getVisibilities() {
        return postRatingsRequestDto.getVisibilities();
    }

    public PostDocRetrievalOrderStrategy getOrder() {
        return postRatingsRequestDto.getOrder();
    }

    public ObjectId getClientId() {
        return postRatingsRequestDto.getClientId();
    }

}
