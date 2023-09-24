package com.msik404.karmaappposts.rating.repository;

import com.msik404.karmaappposts.rating.RatingDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RatingRepository extends MongoRepository<RatingDocument, ObjectId>, CustomRatingRepository {
}
