package com.msik404.karmaappposts.post.position;

import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;

public record PostDocScrollPositionConcrete(@Nullable Long karmaScore, @Nullable ObjectId postId) {

    public boolean isInitial() {
        return karmaScore == null || postId == null;
    }

    public PostDocScrollPositionConcrete() {
        this(null, null);
    }

}
