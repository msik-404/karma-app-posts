package com.msik404.karmaappposts.post.repository;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.criteria.PostDocScrollingCriteria;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPositionConcrete;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final MongoOperations ops;

    @Override
    public List<PostDocument> findFirstN(
            int size,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order) {

        assert !visibilities.isEmpty();

        Query query = position.isInitial() ? new Query() : new Query(
                PostDocScrollingCriteria.getNonInitial(position.getKarmaScore(), position.getPostId()));

        query.addCriteria(Criteria.where("visibility").in(visibilities));

        query.limit(size);
        query.with(order.getOrderStrategy());

        return ops.find(query, PostDocument.class);
    }

}
