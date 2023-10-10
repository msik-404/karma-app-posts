package com.msik404.karmaappposts.post.dto;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public record UserIdOnlyDto(@NonNull ObjectId userId) {
}
