package com.msik404.karmaappposts.post;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "posts")
public class PostDocument {

    public ObjectId id;

    public String userId;

    public String headline;

    public String text;

    private Long karmaScore;

    private Visibility visibility;

}
