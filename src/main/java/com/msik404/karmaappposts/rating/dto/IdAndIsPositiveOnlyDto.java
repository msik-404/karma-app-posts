package com.msik404.karmaappposts.rating.dto;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public record IdAndIsPositiveOnlyDto(@NonNull ObjectId id, @NonNull Boolean isPositive) {
}
