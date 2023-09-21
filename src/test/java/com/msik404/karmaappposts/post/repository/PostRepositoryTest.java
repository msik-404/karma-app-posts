package com.msik404.karmaappposts.post.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.msik404.karmaappposts.MongoConfiguration;
import com.msik404.karmaappposts.TestingDataGenerator;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrder;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPosition;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPositionConcrete;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        MongoConfiguration.class,
        PostRepository.class,
        CustomPostRepository.class,
        CustomPostRepositoryImpl.class
})
@EnableMongoRepositories
@Testcontainers
class PostRepositoryTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test");
    }

    private final MongoOperations ops;
    private final PostRepository repository;

    private final TestingDataGenerator dataGenerator;

    @Autowired
    PostRepositoryTest(MongoTemplate template, PostRepository repository) {

        this.ops = template;
        this.repository = repository;

        dataGenerator = new TestingDataGenerator();
    }

    @BeforeEach
    void setUp() {
        repository.insert(dataGenerator.getTestPostDocs());
    }

    @AfterEach
    void tearDown() {
        ops.dropCollection(ops.getCollectionName(PostDocument.class));
    }

    @Test
    void findAndIncrementKarmaScoreById_IdExists_NewScoreIsAsDesired() {

        // given
        final int idx = 0;
        final int delta = 10;
        final PostDocument preUpdate = dataGenerator.getTestPostDocs().get(idx);
        final PostDocument groundTruth = TestingDataGenerator.getTestPostDoc(
                preUpdate.getId(),
                preUpdate.getUserId(),
                preUpdate.getKarmaScore() + delta,
                preUpdate.getVisibility()
        );

        // when
        assertEquals(1, repository.findAndIncrementKarmaScoreById(preUpdate.getId(), delta));

        // then
        final Optional<PostDocument> optionalUpdatedPostDoc = repository.findById(preUpdate.getId());
        assertTrue(optionalUpdatedPostDoc.isPresent());
        final PostDocument updatedPostDoc = optionalUpdatedPostDoc.get();
        assertEquals(groundTruth, updatedPostDoc);
    }

    @Test
    void findAndIncrementKarmaScoreById_IdDoesNotExists_NoUpdate() {

        // given
        final ObjectId nonPersistedObjectId = ObjectId.get();
        final int delta = 10;

        // when then
        assertEquals(0, repository.findAndIncrementKarmaScoreById(nonPersistedObjectId, delta));
    }

    @Test
    void findAndSetVisibilityById_IdExists_NewVisibilityAsDesired() {

        // given
        final int idx = 0;
        final Visibility newVisibility = Visibility.DELETED;
        final PostDocument preUpdate = dataGenerator.getTestPostDocs().get(idx);
        final PostDocument groundTruth = TestingDataGenerator.getTestPostDoc(
                preUpdate.getId(),
                preUpdate.getUserId(),
                preUpdate.getKarmaScore(),
                newVisibility
        );

        // when
        assertEquals(1, repository.findAndSetVisibilityById(preUpdate.getId(), newVisibility));

        // then
        final Optional<PostDocument> optionalUpdatedPostDoc = repository.findById(preUpdate.getId());
        assertTrue(optionalUpdatedPostDoc.isPresent());
        final PostDocument updatedPostDoc = optionalUpdatedPostDoc.get();
        assertEquals(groundTruth, updatedPostDoc);
    }

    @Test
    void findAndSetVisibilityById_IdDoesNotExists_NoUpdate() {

        // given
        final ObjectId nonPersistedObjectId = ObjectId.get();
        final Visibility newVisibility = Visibility.DELETED;

        // when then
        assertEquals(0, repository.findAndSetVisibilityById(nonPersistedObjectId, newVisibility));
    }

    @Test
    void findFirstN_EnoughDataPersisted_RequestedAmountReturnedInProperOrder() {

        // given
        final int size = 5;
        final List<Visibility> visibilities = List.of(Visibility.ACTIVE);
        final List<PostDocument> groundTruth = dataGenerator.getTestPostDocs().stream()
                .filter(post -> post.getVisibility().equals(visibilities.get(0)))
                .limit(size)
                .toList();

        // when
        final List<PostDocument> results = repository.findFirstN(
                size,
                PostDocScrollPosition.initial(),
                visibilities,
                PostDocRetrievalOrder.desc()
        );

        // then
        assertEquals(size, groundTruth.size());
        for (int i = 0; i < size; i++) {
            assertEquals(groundTruth.get(i), results.get(i));
        }

    }

    @Test
    void findFirstN_NotEnoughDataPersisted_AsMuchAsPossibleReturnedInProperOrder() {

        // given
        final int lastPostDocIdx = 1;
        final PostDocument lastPostDoc = dataGenerator.getTestPostDocs().get(lastPostDocIdx);

        final int size = 15;
        final Set<Visibility> visibilities = Set.of(Visibility.ACTIVE, Visibility.HIDDEN);
        final List<PostDocument> groundTruth = dataGenerator.getTestPostDocs().stream()
                .filter(post -> visibilities.contains(post.getVisibility()))
                .skip(lastPostDocIdx + 1)
                .limit(size)
                .toList();

        // when
        final List<PostDocument> results = repository.findFirstN(
                size,
                PostDocScrollPosition.of(lastPostDoc.getKarmaScore(), lastPostDoc.getId()),
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
    void findFirstN_PositionIsInappropriate_RequestedAmountReturnedInProperOrder() {

        // given
        PostDocScrollPositionConcrete position = PostDocScrollPosition.of(3, ObjectId.get());

        final int size = 5;
        final Set<Visibility> visibilities = Set.of(Visibility.DELETED, Visibility.HIDDEN);
        final List<PostDocument> groundTruth = dataGenerator.getTestPostDocs().stream()
                .filter(post -> visibilities.contains(post.getVisibility()))
                .skip(1)
                .limit(size)
                .toList();

        // when
        final List<PostDocument> results = repository.findFirstN(
                size,
                position,
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