package com.msik404.karmaappposts.rating.dto;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record IdAndIsPositiveOnlyDto(@NonNull ObjectId id, @Nullable Boolean isPositive) {
}
