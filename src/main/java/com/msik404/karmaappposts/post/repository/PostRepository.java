package com.msik404.karmaappposts.post.repository;

import java.util.Optional;

import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.dto.UserIdOnlyDto;
import com.msik404.karmaappposts.post.Visibility;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.lang.NonNull;

public interface PostRepository extends MongoRepository<PostDocument, ObjectId>, CustomPostRepository {

    @Update("{ '$inc' : { 'karmaScore' : ?1 } }")
    long findAndIncrementKarmaScoreById(@NonNull ObjectId id, int increment);

    @Update("{ '$set' : { 'visibility' :  ?1 } }")
    long findAndSetVisibilityById(@NonNull ObjectId id, @NonNull Visibility visibility);

    @Query("{ '_id' :  ?0 }")
    Optional<UserIdOnlyDto> findByPostId(@NonNull ObjectId postId);

}
