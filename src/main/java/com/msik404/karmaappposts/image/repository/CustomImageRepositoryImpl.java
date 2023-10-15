package com.msik404.karmaappposts.image.repository;

import java.util.Optional;

import com.msik404.karmaappposts.image.ImageDocument;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class CustomImageRepositoryImpl implements CustomImageRepository {

    private final MongoOperations ops;

    @NonNull
    @Override
    public Optional<Binary> findImageDataById(@NonNull final ObjectId id) {

        final var query = new Query(Criteria.where("_id").is(id));
        query.fields().exclude("_id");

        final ImageDocument doc = ops.findOne(query, ImageDocument.class);

        if (doc == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(doc.getImageData());
    }

}
