package com.msik404.karmaappposts.rating;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "ratings")
public class PostRatingDocument {

    private String postId;

    private List<Rating> ratings;

}
