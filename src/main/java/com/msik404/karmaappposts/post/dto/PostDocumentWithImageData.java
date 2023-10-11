package com.msik404.karmaappposts.post.dto;

import com.msik404.karmaappposts.post.Visibility;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record PostDocumentWithImageData(@NonNull ObjectId id, @NonNull ObjectId userId, @Nullable String headline,
                                        @Nullable String text, long karmaScore, @NonNull Visibility visibility,
                                        @Nullable Binary imageData) {
}
