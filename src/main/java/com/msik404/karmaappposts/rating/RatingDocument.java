package com.msik404.karmaappposts.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

/*
 * I could embed it in PostDocument but this would mean that at most approximately 16Mb / 13b = 153.846
 * of ratings could be associated with given post. Most liked reddit post has at least 400.000 ratings.
 */

@Data
@AllArgsConstructor
@Document(collection = "ratings")
@CompoundIndex(name = "postId_userId", def = "{'postId': 1, 'userId': 1}")
public class RatingDocument {

    private ObjectId id;

    private ObjectId postId;

    private ObjectId userId;

    private boolean isPositive;

    public RatingDocument(@NonNull ObjectId postId, @NonNull ObjectId userId, boolean isPositive) {

        this.id = ObjectId.get();
        this.postId = postId;
        this.userId = userId;
        this.isPositive = isPositive;
    }

}
