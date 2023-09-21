package com.msik404.karmaappposts.post.repository.position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostDocScrollPositionConcrete {

    private Long karmaScore;
    private ObjectId postId;

    public boolean isInitial() {
        return this.karmaScore == null || this.postId == null;
    }

}
