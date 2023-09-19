package com.msik404.karmaappposts.image;

import com.msik404.karmaappposts.image.repository.CustomImageRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<ImageDocument, ObjectId>, CustomImageRepository {
}
