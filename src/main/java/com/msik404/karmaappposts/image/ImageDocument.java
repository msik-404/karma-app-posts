package com.msik404.karmaappposts.image;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "images")
public class ImageDocument {

    public String postId;

    public Binary imageData;

}
