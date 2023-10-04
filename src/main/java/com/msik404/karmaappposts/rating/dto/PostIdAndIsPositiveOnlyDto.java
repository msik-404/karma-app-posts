package com.msik404.karmaappposts.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@AllArgsConstructor
public class PostIdAndIsPositiveOnlyDto {

    @Field("_id")
    private ObjectId postId;

    @Field("isPositive")
    private Boolean isPositive;

}
