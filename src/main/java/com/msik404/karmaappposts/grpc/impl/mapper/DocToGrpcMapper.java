package com.msik404.karmaappposts.grpc.impl.mapper;

import com.msik404.karmaappposts.grpc.Post;
import com.msik404.karmaappposts.grpc.PostRating;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;

public class DocToGrpcMapper {

    public static Post map(PostDocument doc) {

        Post.Builder builder = Post.newBuilder();

        if (doc.getId() != null) {
            builder.setPostId(doc.getId().toString());
        }
        if (doc.getUserId() != null) {
            builder.setUserId(doc.getUserId().toString());
        }
        if (doc.headline != null) {
            builder.setHeadline(doc.getHeadline());
        }
        if (doc.getText() != null) {
            builder.setText(doc.getText());
        }
        if (doc.getKarmaScore() != null) {
            builder.setKarmaScore(doc.getKarmaScore());
        }
        if (doc.getVisibility() != null) {
            builder.setVisibility(VisibilityMapper.map(doc.getVisibility()));
        }

        return builder.build();
    }

    public static PostRating map(IdAndIsPositiveOnlyDto doc) {

        PostRating.Builder builder = PostRating.newBuilder();

        if (doc.id() != null) {
            builder.setPostId(doc.id().toString());
        }
        if (doc.isPositive() != null) {
            builder.setIsPositive(doc.isPositive());
        }

        return builder.build();
    }

}
