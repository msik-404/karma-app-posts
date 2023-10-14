package com.msik404.karmaappposts.grpc.impl.mapper;

import com.google.protobuf.ByteString;
import com.msik404.karmaappposts.grpc.MongoObjectId;
import com.msik404.karmaappposts.grpc.Post;
import com.msik404.karmaappposts.grpc.PostRating;
import com.msik404.karmaappposts.grpc.PostWithImageData;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.dto.PostDocumentWithImageData;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
import org.springframework.lang.NonNull;

public class DocToGrpcMapper {

    @NonNull
    public static Post map(@NonNull PostDocument doc) {

        final Post.Builder builder = Post.newBuilder();

        builder.setPostId(MongoObjectId.newBuilder().setHexString(doc.getId().toHexString()).build());

        builder.setUserId(MongoObjectId.newBuilder().setHexString(doc.getUserId().toHexString()).build());

        if (doc.headline != null) {
            builder.setHeadline(doc.getHeadline());
        }
        if (doc.getText() != null) {
            builder.setText(doc.getText());
        }

        builder.setKarmaScore(doc.getKarmaScore());

        builder.setVisibility(VisibilityMapper.map(doc.getVisibility()));

        return builder.build();
    }

    @NonNull
    public static PostRating map(@NonNull IdAndIsPositiveOnlyDto doc) {

        return PostRating.newBuilder()
                .setPostId(MongoObjectId.newBuilder().setHexString(doc.id().toHexString()).build())
                .setIsPositive(doc.isPositive())
                .build();
    }

    @NonNull
    public static PostWithImageData map(@NonNull PostDocumentWithImageData doc) {

        final PostWithImageData.Builder builder = PostWithImageData.newBuilder();

        builder.setPost(map((PostDocument) doc));

        if (doc.getImageData() != null) {
            builder.setImageData(ByteString.copyFrom(doc.getImageData().getData()));
        }

        return builder.build();
    }

}
