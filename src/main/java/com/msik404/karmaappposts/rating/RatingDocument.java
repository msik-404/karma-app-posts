package com.msik404.karmaappposts.rating;

import org.springframework.data.mongodb.core.mapping.Document;

/*
 * I could embed it in PostDocument but this would mean that at most approximately 16Mb / 13b = 153.846
 * of ratings could be associated with given post. Most liked reddit post has at least 400.000 ratings.
 */

@Document(collation = "ratings")
public class RatingDocument {

    private String postId;

    private String userId;

    private boolean isPositive;

}
