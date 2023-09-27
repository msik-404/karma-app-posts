package com.msik404.karmaappposts.grpc.impl.mapper;

import com.msik404.karmaappposts.grpc.Post;
import com.msik404.karmaappposts.grpc.PostRating;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.rating.dto.PostIdAndIsPositiveOnlyDto;

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

    public static PostRating map(PostIdAndIsPositiveOnlyDto doc) {

        PostRating.Builder builder = PostRating.newBuilder();

        if (doc.getPostId() != null) {
            builder.setPostId(doc.getPostId().toString());
        }
        if (doc.getIsPositive() != null) {
            builder.setIsPositive(doc.getIsPositive());
        }

        return builder.build();
    }

}
