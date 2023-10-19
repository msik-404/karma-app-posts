package com.msik404.karmaappposts.post.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.msik404.karmaappposts.image.ImageDocument;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.dto.PostDocumentWithImageData;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import com.msik404.karmaappposts.post.repository.criteria.PostDocScrollingCriteria;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final MongoOperations ops;

    @NonNull
    private List<PostDocument> findFirstNImpl(
            int size,
            @Nullable ObjectId creatorId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order) {

        assert !visibilities.isEmpty();

        Query query = position.isInitial() ? new Query() : new Query(
                PostDocScrollingCriteria.getNonInitial(position.karmaScore(), position.postId()));

        query.addCriteria(Criteria.where("visibility").in(visibilities));

        if (creatorId != null) {
            query.addCriteria(Criteria.where("userId").is(creatorId));
        }

        query.limit(size);
        query.with(order.getOrderStrategy());

        return ops.find(query, PostDocument.class);
    }

    @NonNull
    @Override
    public List<PostDocument> findFirstN(
            int size,
            @NonNull ObjectId creatorId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order) {

        return findFirstNImpl(size, creatorId, position, visibilities, order);
    }

    @NonNull
    @Override
    public List<PostDocument> findFirstN(
            int size,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order) {

        return findFirstNImpl(size, null, position, visibilities, order);
    }

    @NonNull
    @Override
    public Optional<PostDocumentWithImageData> findDocumentWithImageData(@NonNull ObjectId postId) {

        List<AggregationOperation> aggOps = new ArrayList<>();

        // match
        aggOps.add(new MatchOperation(Criteria.where("_id").is(postId)));

        // lookup
        aggOps.add(new LookupOperation(
                Fields.field(ops.getCollectionName(ImageDocument.class)),
                Fields.field("_id"),
                Fields.field("_id"),
                Fields.field("image_docs")
        ));

        // projection
        AggregationExpression arrayIsNotEmpty = ComparisonOperators.Gt.valueOf(
                        ArrayOperators.Size.lengthOfArray("image_docs.imageData"))
                .greaterThanValue(0);

        AggregationExpression getFirstEl = ArrayOperators.arrayOf("image_docs.imageData")
                .elementAt(0);

        aggOps.add(Aggregation.project(PostDocument.class).and("image_docs.imageData")
                .applyCondition(ConditionalOperators.Cond.when(arrayIsNotEmpty)
                        .thenValueOf(getFirstEl)
                        .otherwise("$$REMOVE"))
        );

        TypedAggregation<PostDocument> agg = Aggregation.newAggregation(PostDocument.class, aggOps);

        AggregationResults<PostDocumentWithImageData> aggResults = ops.aggregate(
                agg, PostDocumentWithImageData.class);

        if (aggResults.getMappedResults().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(aggResults.getMappedResults().get(0));
    }

}
