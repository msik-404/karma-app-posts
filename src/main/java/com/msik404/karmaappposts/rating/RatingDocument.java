package com.msik404.karmaappposts.rating;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/*
 * I could embed it in PostDocument but this would mean that at most approximately 16Mb / 13b = 153.846
 * of ratings could be associated with given post. Most liked reddit post has at least 400.000 ratings.
 */

@AllArgsConstructor
@Document(collection = "ratings")
@CompoundIndex(name = "postId_userId", def = "{'_id': 1, 'userId': 1}")
public class RatingDocument {

    @Id
    private ObjectId postId;

    private ObjectId userId;

    private boolean isPositive;

}
