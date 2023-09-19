package com.msik404.karmaappposts.post;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "posts")
@CompoundIndex(name = "karmaScore_id", def = "{'karmaScore': -1, '_id': 1}")
public class PostDocument {

    public ObjectId id;

    public ObjectId userId;

    public String headline;

    public String text;

    private Long karmaScore;

    private Visibility visibility;

}
