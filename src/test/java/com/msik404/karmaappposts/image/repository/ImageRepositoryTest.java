package com.msik404.karmaappposts.image.repository;

import java.util.Optional;

import com.msik404.karmaappposts.MongoConfiguration;
import com.msik404.karmaappposts.TestingDataGenerator;
import com.msik404.karmaappposts.image.ImageDocument;
import org.bson.types.Binary;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        MongoConfiguration.class,
        ImageRepository.class,
        CustomImageRepository.class,
        CustomImageRepositoryImpl.class
})
@EnableMongoRepositories
@Testcontainers
class ImageRepositoryTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test");
    }

    private final MongoOperations ops;
    private final ImageRepository repository;

    private final TestingDataGenerator dataGenerator;

    @Autowired
    ImageRepositoryTest(MongoTemplate template, ImageRepository repository) {

        this.ops = template;
        this.repository = repository;

        dataGenerator = new TestingDataGenerator();
    }

    @BeforeEach
    void setUp() {
        repository.insert(dataGenerator.getTestImageDocs());
    }

    @AfterEach
    void tearDown() {
        ops.dropCollection(ops.getCollectionName(ImageDocument.class));
    }

    @Test
    void findImageDataById_RequestedDocExistsAndHasImage_OnlyImageDataIsFetched() {

        // given
        final int idx = 0;
        final var groundTruth = dataGenerator.getTestImageDocs().get(idx);

        // when
        final Optional<Binary> optionalImageData = repository.findImageDataById(groundTruth.getPostId());

        // then
        assertTrue(optionalImageData.isPresent());
        assertArrayEquals(groundTruth.getImageData().getData(), optionalImageData.get().getData());
    }

    @Test
    void findImageDataById_RequestedDocDoesNotExist_OptionalEmpty() {

        // given
        final ObjectId nonExistingId = ObjectId.get();

        // when
        final Optional<Binary> optionalImageData = repository.findImageDataById(nonExistingId);

        // then
        assertTrue(optionalImageData.isEmpty());
    }

}
