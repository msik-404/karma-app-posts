package com.msik404.karmaappposts.post.dto;

import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;


@Data
@EqualsAndHashCode(callSuper = true)
public class PostDocumentWithImageData extends PostDocument {

    private Binary imageData;

    public PostDocumentWithImageData(
            @NonNull ObjectId id,
            @NonNull ObjectId userId,
            @Nullable String headline,
            @Nullable String text,
            long karmaScore,
            @NonNull Visibility visibility,
            @Nullable Binary imageData) {

        super(id, userId, headline, text, karmaScore, visibility);

        this.imageData = imageData;
    }
}
