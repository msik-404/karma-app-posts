package com.msik404.karmaappposts.image.repository;

import com.msik404.karmaappposts.image.ImageDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<ImageDocument, ObjectId>, CustomImageRepository {
}
