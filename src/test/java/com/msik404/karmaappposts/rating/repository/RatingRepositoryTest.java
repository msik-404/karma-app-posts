package com.msik404.karmaappposts.rating.repository;

import java.util.List;

import com.msik404.karmaappposts.MongoConfiguration;
import com.msik404.karmaappposts.TestingDataGenerator;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.CustomPostRepository;
import com.msik404.karmaappposts.post.repository.CustomPostRepositoryImpl;
import com.msik404.karmaappposts.post.repository.PostRepository;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrder;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPosition;
import com.msik404.karmaappposts.rating.dto.RatingDocDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test");
    }

    private final MongoOperations ops;
    private final PostRepository postRepository;
    private final RatingRepository ratingRepository;

    private final TestingDataGenerator dataGenerator;

    @Autowired
    RatingRepositoryTest(MongoTemplate template, PostRepository postRepository, RatingRepository ratingRepository) {

        this.ops = template;
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
        ops.dropCollection(ops.getCollectionName(PostDocument.class));
    }

    @Test
    void findFirstN_InitialAndNotEnoughPosts_AsMuchAsPossibleReturnedInProperOrder() {

        // given
        final int userId = 2;
        final ObjectId userObjectId = TestingDataGenerator.TEST_USER_IDS.get(userId);
        final int size = 30;
        final List<Visibility> visibilities = List.of(Visibility.ACTIVE, Visibility.HIDDEN, Visibility.DELETED);
        final List<RatingDocDto> groundTruth = dataGenerator.getRatings(userObjectId, visibilities, size, 0);

        // when
        final List<RatingDocDto> results = ratingRepository.findFirstN(
                size,
                userObjectId,
                PostDocScrollPosition.initial(),
                visibilities,
                PostDocRetrievalOrder.desc()
        );

        // then
        assertEquals(groundTruth.size(), results.size());
        for (int i = 0; i < groundTruth.size(); i++) {
            assertEquals(groundTruth.get(i), results.get(i));
        }
    }

    @Test
    void findFirstN_NonInitialAndNotEnoughPosts_AsMuchAsPossibleReturnedInProperOrder() {

        // given
        final int userId = 2;
        final ObjectId userObjectId = TestingDataGenerator.TEST_USER_IDS.get(userId);
        final int size = 30;
        final int skip = 3;
        final PostDocument lastDoc = dataGenerator.getTestPostDocs().get(skip-1);
        final List<Visibility> visibilities = List.of(Visibility.ACTIVE, Visibility.HIDDEN);
        final List<RatingDocDto> groundTruth = dataGenerator.getRatings(userObjectId, visibilities, size, skip);

        // when
        final List<RatingDocDto> results = ratingRepository.findFirstN(
                size,
                userObjectId,
                PostDocScrollPosition.of(lastDoc.getKarmaScore(), lastDoc.getId()),
                visibilities,
                PostDocRetrievalOrder.desc()
        );

        // then
        assertEquals(groundTruth.size(), results.size());
        for (int i = 0; i < groundTruth.size(); i++) {
            assertEquals(groundTruth.get(i), results.get(i));
        }
    }

}