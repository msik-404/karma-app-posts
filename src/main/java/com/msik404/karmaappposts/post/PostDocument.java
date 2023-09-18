package com.msik404.karmaappposts.post;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "posts")
public class PostDocument {

    public String id;

    public String userId;

    public String headline;

    public String text;

    private Long karmaScore;

    private Visibility visibility;

}
