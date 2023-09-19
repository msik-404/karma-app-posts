package com.msik404.karmaappposts.rating;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RatingRepository extends MongoRepository<RatingDocument, ObjectId> {
}
