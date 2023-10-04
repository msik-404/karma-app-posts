package com.msik404.karmaappposts.rating.dto;

import org.bson.types.ObjectId;

public record IdAndIsPositiveOnlyDto(ObjectId id, Boolean isPositive) {
}
