package com.msik404.karmaappposts.image.repository;

import java.util.Optional;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public interface CustomImageRepository {

    @NonNull
    Optional<Binary> findImageDataById(@NonNull ObjectId id);

}
