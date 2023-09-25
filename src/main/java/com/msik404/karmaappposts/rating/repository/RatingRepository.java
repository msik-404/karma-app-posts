package com.msik404.karmaappposts.rating.repository;

import java.util.Optional;

import com.msik404.karmaappposts.rating.RatingDocument;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.lang.NonNull;

public interface RatingRepository extends MongoRepository<RatingDocument, ObjectId>, CustomRatingRepository {

    Optional<IdAndIsPositiveOnlyDto> findByPostIdAndUserId(@NonNull ObjectId postId, @NonNull ObjectId userId);

    @Update("{ '$set' : { 'isPositive' : ?1 } }")
    long findAndSetIsPositiveById(@NonNull ObjectId id, boolean isPositive);

}
