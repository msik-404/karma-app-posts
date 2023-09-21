package com.msik404.karmaappposts.post.repository.position;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public class PostDocScrollPosition {

    public static PostDocScrollPositionConcrete initial() {
        return new PostDocScrollPositionConcrete();
    }

    public static PostDocScrollPositionConcrete of(long karmaScore, @NonNull ObjectId postId) {
        return new PostDocScrollPositionConcrete(karmaScore, postId);
    }

}
