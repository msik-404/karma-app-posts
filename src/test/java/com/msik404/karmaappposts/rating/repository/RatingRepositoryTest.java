package com.msik404.karmaappposts.rating.repository;

import java.util.List;
import java.util.Optional;

import com.msik404.karmaappposts.MongoConfiguration;
import com.msik404.karmaappposts.TestingDataGenerator;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrder;
import com.msik404.karmaappposts.post.position.PostDocScrollPosition;
import com.msik404.karmaappposts.post.repository.CustomPostRepository;
import com.msik404.karmaappposts.post.repository.CustomPostRepositoryImpl;
import com.msik404.karmaappposts.post.repository.PostRepository;
import com.msik404.karmaappposts.rating.RatingDocument;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        // config bean
        MongoConfiguration.class,
        // post repository beans
        PostRepository.class,
        CustomPostRepository.class,
        CustomPostRepositoryImpl.class,
        // rating repository beans
        RatingRepository.class,
        CustomRatingRepository.class,
        CustomRatingRepositoryImpl.class
})
@EnableMongoRepositories(basePackageClasses = {PostRepository.class, RatingRepository.class})
@Testcontainers
class RatingRepositoryTest {

    public static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    static {
        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test");
    }

    private final PostRepository postRepository;
    private final RatingRepository ratingRepository;

    private final TestingDataGenerator dataGenerator;

    @Autowired
    RatingRepositoryTest(PostRepository postRepository, RatingRepository ratingRepository) {

        this.postRepository = postRepository;
        this.ratingRepository = ratingRepository;

        dataGenerator = new TestingDataGenerator();
    }

    @BeforeEach
    void setUp() {

        postRepository.insert(dataGenerator.getTestPostDocs());
        ratingRepository.insert(dataGenerator.getTestRatingDocs());
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        ratingRepository.deleteAll();
    }

    @Test
    void findFirstN_InitialAndNotEnoughPosts_AsMuchAsPossibleReturnedInProperOrder() {

        // given
        int userId = 2;
        ObjectId clientObjectId = TestingDataGenerator.TEST_USER_IDS.get(userId);
        int size = 30;
        List<Visibility> visibilities = List.of(Visibility.ACTIVE, Visibility.HIDDEN, Visibility.DELETED);
        List<IdAndIsPositiveOnlyDto> groundTruth = TestingDataGenerator.getRatings(
                dataGenerator.getTestPostDocs(),
                dataGenerator.getTestRatingDocs(),
                clientObjectId,
                visibilities
        );

        groundTruth = groundTruth.subList(0, Math.min(groundTruth.size(), size));

        // when
        List<IdAndIsPositiveOnlyDto> results = ratingRepository.findFirstN(
                size,
                clientObjectId,
                PostDocScrollPosition.initial(),
                visibilities,
                PostDocRetrievalOrder.desc());

        // then
        assertEquals(groundTruth.size(), results.size());
        for (int i = 0; i < groundTruth.size(); i++) {
            assertEquals(groundTruth.get(i), results.get(i));
        }
    }

    @Test
    void findFirstN_NonInitialAndNotEnoughPosts_AsMuchAsPossibleReturnedInProperOrder() {

        // given
        int userId = 2;
        ObjectId clientObjectId = TestingDataGenerator.TEST_USER_IDS.get(userId);
        int size = 30;
        int skip = 3;
        PostDocument lastDoc = dataGenerator.getTestPostDocs().get(skip - 1);
        List<Visibility> visibilities = List.of(Visibility.ACTIVE, Visibility.HIDDEN);
        List<IdAndIsPositiveOnlyDto> groundTruth = TestingDataGenerator.getRatings(
                dataGenerator.getTestPostDocs(),
                dataGenerator.getTestRatingDocs(),
                clientObjectId,
                visibilities
        );

        groundTruth = groundTruth.subList(skip, Math.min(groundTruth.size(), size));

        // when
        List<IdAndIsPositiveOnlyDto> results = ratingRepository.findFirstN(
                size,
                clientObjectId,
                PostDocScrollPosition.of(lastDoc.getKarmaScore(), lastDoc.getId()),
                visibilities,
                PostDocRetrievalOrder.desc());

        // then
        assertEquals(groundTruth.size(), results.size());
        for (int i = 0; i < groundTruth.size(); i++) {
            assertEquals(groundTruth.get(i), results.get(i));
        }
    }

    @Test
    void findFirstN_NonInitialAndNotEnoughPostsCreatorIdProvided_AsMuchAsPossibleReturnedInProperOrder() {

        // given
        int clientId = 2;
        ObjectId clientObjectId = TestingDataGenerator.TEST_USER_IDS.get(clientId);

        int creatorId = 0;
        ObjectId creatorObjectId = TestingDataGenerator.TEST_USER_IDS.get(creatorId);

        int size = 30;
        int skip = 1;

        List<PostDocument> posts = dataGenerator.getTestPostDocs().stream().
                filter(post -> post.getUserId().equals(creatorObjectId))
                .toList();
        PostDocument lastDoc = posts.get(skip - 1);

        List<Visibility> visibilities = List.of(Visibility.ACTIVE, Visibility.HIDDEN);
        List<IdAndIsPositiveOnlyDto> groundTruth = TestingDataGenerator.getRatings(
                posts,
                dataGenerator.getTestRatingDocs(),
                clientObjectId,
                visibilities
        );

        groundTruth = groundTruth.subList(skip, Math.min(groundTruth.size(), size));

        // when
        List<IdAndIsPositiveOnlyDto> results = ratingRepository.findFirstN(
                size,
                creatorObjectId,
                clientObjectId,
                PostDocScrollPosition.of(lastDoc.getKarmaScore(), lastDoc.getId()),
                visibilities,
                PostDocRetrievalOrder.desc());

        // then
        assertEquals(groundTruth.size(), results.size());
        for (int i = 0; i < groundTruth.size(); i++) {
            assertEquals(groundTruth.get(i), results.get(i));
        }
    }

    @Test
    void findByPostIdAndUserId_RequestedDocIsPersisted_RequestedDocIsReturned() {

        // given
        RatingDocument doc = dataGenerator.getTestRatingDocs().get(0);
        var groundTruth = new IdAndIsPositiveOnlyDto(doc.getId(), doc.isPositive());

        // when
        Optional<IdAndIsPositiveOnlyDto> result = ratingRepository.findByPostIdAndUserId(
                doc.getPostId(), doc.getUserId());

        // then
        assertTrue(result.isPresent());
        assertEquals(groundTruth, result.get());
    }

    @Test
    void findByPostIdAndUserId_RequestedDocIsNotPersisted_EmptyOptionalIsReturned() {

        // given when
        Optional<IdAndIsPositiveOnlyDto> result = ratingRepository.findByPostIdAndUserId(
                ObjectId.get(), ObjectId.get());

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void findAndSetIsPositiveById_RequestedDocIsPresent_IsPositiveGetsUpdated() {

        // given
        boolean newIsPositive = false;

        RatingDocument doc = dataGenerator.getTestRatingDocs().get(0);

        assertTrue(doc.isPositive());

        // when
        long result = ratingRepository.findAndSetIsPositiveById(doc.getId(), newIsPositive);

        // then
        assertEquals(1, result);
        Optional<RatingDocument> optionalDoc = ratingRepository.findById(doc.getId());
        assertTrue(optionalDoc.isPresent());
        var updatedDoc = optionalDoc.get();
        assertEquals(newIsPositive, updatedDoc.isPositive());
    }

    @Test
    void findAndSetIsPositiveById_RequestedDocIsNotPresent_IsPositiveGetsNotUpdated() {
        // given
        boolean newIsPositive = false;

        // when then
        assertEquals(0, ratingRepository.findAndSetIsPositiveById(ObjectId.get(), newIsPositive));
    }

}