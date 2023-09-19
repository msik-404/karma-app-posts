package com.msik404.karmaappposts.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "images")
@Getter
@AllArgsConstructor
public class ImageDocument {

    private ObjectId id;

    private Binary imageData;

}
