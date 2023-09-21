package com.msik404.karmaappposts.post.repository.criteria;

import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;

public class PostDocScrollingCriteria {

    public static Criteria getNonInitial(long karmaScore, @NonNull ObjectId postId) {
        return new Criteria().orOperator(
                Criteria.where("karmaScore").lt(karmaScore),
                Criteria.where("karmaScore").is(karmaScore).and("_id").gt(postId)
        );
    }

}
