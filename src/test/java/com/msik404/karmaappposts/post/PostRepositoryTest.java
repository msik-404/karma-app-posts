package com.msik404.karmaappposts.post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.msik404.karmaappposts.MongoConfiguration;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.lang.NonNull;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        MongoConfiguration.class,
        PostRepository.class
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

    @Autowired
    PostRepositoryTest(MongoTemplate template, PostRepository repository) {

        this.ops = template;
        this.repository = repository;
    }

    private static PostDocument getTestPostDoc(
            @NonNull ObjectId id,
            @NonNull ObjectId userId,
            long karmaScore,
            @NonNull Visibility visibility) {

        return PostDocument.builder()
                .id(id)
                .userId(userId)
                .karmaScore(karmaScore)
                .visibility(visibility)
                .build();
    }

    private static List<PostDocument> getTestPostDocs() {

        final int size = 5;
        List<PostDocument> docs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            docs.add(getTestPostDoc(ObjectId.get(), ObjectId.get(), i, Visibility.ACTIVE));
        }
        return docs;
    }

    private static final List<PostDocument> TEST_POST_DOCS = getTestPostDocs();

    @BeforeEach
    void setUp() {
        repository.insert(TEST_POST_DOCS);
    }

    @AfterEach
    void tearDown() {
        ops.dropCollection(ops.getCollectionName(PostDocument.class));
    }

    @Test
    void findAndIncrementKarmaScoreById() {

        // given
        final int idx = 0;
        final int delta = 10;
        final PostDocument preUpdate = TEST_POST_DOCS.get(idx);
        final PostDocument groundTruth = getTestPostDoc(
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
    void findAndSetVisibilityById() {

        // given
        final int idx = 0;
        final Visibility newVisibility = Visibility.DELETED;
        final PostDocument preUpdate = TEST_POST_DOCS.get(idx);
        final PostDocument groundTruth = getTestPostDoc(
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

}