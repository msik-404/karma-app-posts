package com.msik404.karmaappposts.post;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.lang.NonNull;

public interface PostRepository extends MongoRepository<PostDocument, ObjectId> {

    @Update("{ '$inc' : { 'karmaScore' : ?1 } }")
    long findAndIncrementKarmaScoreById(@NonNull ObjectId id, int increment);

    @Update("{ '$set' : { 'visibility' :  ?1 } }")
    long findAndSetVisibilityById(@NonNull ObjectId id, @NonNull Visibility visibility);

}
