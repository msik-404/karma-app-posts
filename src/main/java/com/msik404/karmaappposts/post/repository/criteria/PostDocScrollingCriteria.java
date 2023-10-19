package com.msik404.karmaappposts.post.repository.criteria;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.lang.NonNull;

public class PostDocScrollingCriteria {

    @NonNull
    public static Criteria getNonInitial(long karmaScore, @NonNull ObjectId postId) {
        return new Criteria().orOperator(
                Criteria.where("karmaScore").lt(karmaScore),
                Criteria.where("karmaScore").is(karmaScore).and("_id").gt(postId)
        );
    }

}
