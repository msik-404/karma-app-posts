package com.msik404.karmaappposts.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    public PostDocument(
            @NonNull ObjectId userId,
            @Nullable String headline,
            @Nullable String text) {

        this.id = ObjectId.get();
        this.userId = userId;
        this.headline = headline;
        this.text = text;
        this.karmaScore = 0L;
        this.visibility = Visibility.ACTIVE;
    }

}
