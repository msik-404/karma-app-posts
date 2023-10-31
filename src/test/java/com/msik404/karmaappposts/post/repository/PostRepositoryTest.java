package com.msik404.karmaappposts.post.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappposts.MongoConfiguration;
import com.msik404.karmaappposts.TestingDataGenerator;
import com.msik404.karmaappposts.image.ImageDocument;
import com.msik404.karmaappposts.image.repository.CustomImageRepository;
import com.msik404.karmaappposts.image.repository.CustomImageRepositoryImpl;
import com.msik404.karmaappposts.image.repository.ImageRepository;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.dto.PostDocumentWithImageData;
import com.msik404.karmaappposts.post.dto.UserIdOnlyDto;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrder;
import com.msik404.karmaappposts.post.position.PostDocScrollPosition;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        // config bean
        MongoConfiguration.class,
        // post repository beans
        PostRepository.class,
        CustomPostRepository.class,
        CustomPostRepositoryImpl.class,
        // image repository beans
        ImageRepository.class,
        CustomImageRepository.class,
        CustomImageRepositoryImpl.class
})
@EnableMongoRepositories(basePackageClasses = {PostRepository.class, ImageRepository.class})
@Testcontainers
class PostRepositoryTest {

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

    private final PostRepository repository;
    private final ImageRepository imageRepository;

    private final TestingDataGenerator dataGenerator;

    @Autowired
    PostRepositoryTest(PostRepository repository, ImageRepository imageRepository) {

        this.repository = repository;
        this.imageRepository = imageRepository;

        dataGenerator = new TestingDataGenerator();
    }

    @BeforeEach
    void setUp() {
        repository.insert(dataGenerator.getTestPostDocs());
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void findAndIncrementKarmaScoreById_IdExists_NewScoreIsAsDesired() {

        // given
        int idx = 0;
        int delta = 10;
        PostDocument preUpdate = dataGenerator.getTestPostDocs().get(idx);
        PostDocument groundTruth = TestingDataGenerator.getTestPostDoc(
                preUpdate.getId(),
                preUpdate.getUserId(),
                preUpdate.getKarmaScore() + delta,
                preUpdate.getVisibility()
        );

        // when
        assertEquals(1, repository.findAndIncrementKarmaScoreById(preUpdate.getId(), delta));

        // then
        Optional<PostDocument> optionalUpdatedPostDoc = repository.findById(preUpdate.getId());
        assertTrue(optionalUpdatedPostDoc.isPresent());
        PostDocument updatedPostDoc = optionalUpdatedPostDoc.get();
        assertEquals(groundTruth, updatedPostDoc);
    }

    @Test
    void findAndIncrementKarmaScoreById_IdDoesNotExists_NoUpdate() {

        // given
        ObjectId nonPersistedObjectId = ObjectId.get();
        int delta = 10;

        // when then
        assertEquals(0, repository.findAndIncrementKarmaScoreById(nonPersistedObjectId, delta));
    }

    @Test
    void findAndSetVisibilityById_IdExists_NewVisibilityAsDesired() {

        // given
        int idx = 0;
        Visibility newVisibility = Visibility.DELETED;
        PostDocument preUpdate = dataGenerator.getTestPostDocs().get(idx);
        PostDocument groundTruth = TestingDataGenerator.getTestPostDoc(
                preUpdate.getId(),
                preUpdate.getUserId(),
                preUpdate.getKarmaScore(),
                newVisibility
        );

        // when
        UpdateResult result = repository.findAndSetVisibilityById(preUpdate.getId(), newVisibility);

        // then
        assertEquals(1, result.getMatchedCount());
        Optional<PostDocument> optionalUpdatedPostDoc = repository.findById(preUpdate.getId());
        assertTrue(optionalUpdatedPostDoc.isPresent());
        PostDocument updatedPostDoc = optionalUpdatedPostDoc.get();
        assertEquals(groundTruth, updatedPostDoc);
    }

    @Test
    void findAndSetVisibilityById_IdDoesNotExists_NoUpdate() {

        // given
        ObjectId nonPersistedObjectId = ObjectId.get();
        Visibility newVisibility = Visibility.DELETED;

        // when
        UpdateResult result = repository.findAndSetVisibilityById(nonPersistedObjectId, newVisibility);

        // then
        assertEquals(0, result.getMatchedCount());
    }

    @Test
    void findFirstN_EnoughDataPersisted_RequestedAmountReturnedInProperOrder() {

        // given
        int size = 5;
        List<Visibility> visibilities = List.of(Visibility.ACTIVE);
        List<PostDocument> groundTruth = dataGenerator.getTestPostDocs().stream()
                .filter(post -> post.getVisibility().equals(visibilities.get(0)))
                .limit(size)
                .toList();

        // when
        List<PostDocument> results = repository.findFirstN(
                size,
                PostDocScrollPosition.initial(),
                visibilities,
                PostDocRetrievalOrder.desc());

        // then
        assertEquals(size, groundTruth.size());
        for (int i = 0; i < size; i++) {
            assertEquals(groundTruth.get(i), results.get(i));
        }

    }

    @Test
    void findFirstN_NotEnoughDataPersisted_AsMuchAsPossibleReturnedInProperOrder() {

        // given
        int lastPostDocIdx = 1;
        PostDocument lastPostDoc = dataGenerator.getTestPostDocs().get(lastPostDocIdx);

        int size = 15;
        Set<Visibility> visibilities = Set.of(Visibility.ACTIVE, Visibility.HIDDEN);
        List<PostDocument> groundTruth = dataGenerator.getTestPostDocs().stream()
                .filter(post -> visibilities.contains(post.getVisibility()))
                .skip(lastPostDocIdx + 1)
                .limit(size)
                .toList();

        // when
        List<PostDocument> results = repository.findFirstN(
                size,
                PostDocScrollPosition.of(lastPostDoc.getKarmaScore(), lastPostDoc.getId()),
                visibilities,
                PostDocRetrievalOrder.desc());

        // then
        assertEquals(groundTruth.size(), results.size());
        for (int i = 0; i < groundTruth.size(); i++) {
            assertEquals(groundTruth.get(i), results.get(i));
        }

    }

    @Test
    void findFirstN_NotEnoughDataPersistedCreatorIdProvided_AsMuchAsPossibleReturnedInProperOrder() {

        // given
        int lastPostDocIdx = 0;
        ObjectId creatorId = TestingDataGenerator.TEST_USER_IDS.get(0);

        int size = 15;
        Set<Visibility> visibilities = Set.of(Visibility.ACTIVE, Visibility.HIDDEN);
        List<PostDocument> groundTruth = dataGenerator.getTestPostDocs().stream()
                .filter(post -> visibilities.contains(post.getVisibility()) && post.getUserId().equals(creatorId))
                .toList();

        PostDocument lastPostDoc = groundTruth.get(lastPostDocIdx);
        groundTruth = groundTruth.subList(1, 3);

        // when
        List<PostDocument> results = repository.findFirstN(
                size,
                creatorId,
                PostDocScrollPosition.of(lastPostDoc.getKarmaScore(), lastPostDoc.getId()),
                visibilities,
                PostDocRetrievalOrder.desc());

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

        int size = 5;
        Set<Visibility> visibilities = Set.of(Visibility.DELETED, Visibility.HIDDEN);
        List<PostDocument> groundTruth = dataGenerator.getTestPostDocs().stream()
                .filter(post -> visibilities.contains(post.getVisibility()))
                .skip(1)
                .limit(size)
                .toList();

        // when
        List<PostDocument> results = repository.findFirstN(
                size,
                position,
                visibilities,
                PostDocRetrievalOrder.desc());

        // then
        assertEquals(groundTruth.size(), results.size());
        for (int i = 0; i < groundTruth.size(); i++) {
            assertEquals(groundTruth.get(i), results.get(i));
        }

    }

    @Test
    void findCreatorIdByPostId_PostExists_CreatorIsFound() {

        // given
        PostDocument post = dataGenerator.getTestPostDocs().get(0);
        ObjectId postId = post.getId();

        // when
        Optional<UserIdOnlyDto> optionalObjectId = repository.findUserIdById(postId);

        // then
        assertTrue(optionalObjectId.isPresent());
        assertEquals(post.getUserId(), optionalObjectId.get().userId());
    }

    @Test
    void findCreatorIdByPostId_PostDoesNotExist_CreatorIsNotFound() {

        // given
        ObjectId postId = ObjectId.get();

        // when
        Optional<UserIdOnlyDto> optionalObjectId = repository.findUserIdById(postId);

        // then
        assertTrue(optionalObjectId.isEmpty());
    }

    @Test
    void findDocumentWithImageData_PostDocumentAndImageDocumentExist_PostDocumentWithImageDataIsReturned() {

        // save image docs
        imageRepository.insert(dataGenerator.getTestImageDocs());

        // given
        int postDocIdx = 0;
        PostDocument postDocument = dataGenerator.getTestPostDocs().get(postDocIdx);

        int imageDocIdx = 0;
        ImageDocument imageDocument = dataGenerator.getTestImageDocs().get(imageDocIdx);

        assertEquals(postDocument.getId(), imageDocument.getPostId());

        PostDocumentWithImageData groundTruth = new PostDocumentWithImageData(
                postDocument.getId(),
                postDocument.getUserId(),
                postDocument.getHeadline(),
                postDocument.getText(),
                postDocument.getKarmaScore(),
                postDocument.getVisibility(),
                imageDocument.getImageData()
        );

        // when
        Optional<PostDocumentWithImageData> optionalResult = repository.findDocumentWithImageDataById(
                postDocument.getId());

        // then
        assertTrue(optionalResult.isPresent());
        assertEquals(groundTruth, optionalResult.get());

        // clear image docs
        imageRepository.deleteAll();
    }

    @Test
    void findDocumentWithImageData_PostDocumentExistsAndImageDocumentDoesNotExist_PostDocumentWithImageDataIsReturnedWithNullImageDataField() {

        // save image docs
        imageRepository.insert(dataGenerator.getTestImageDocs());

        // given
        int postDocIdx = 4;
        PostDocument postDocument = dataGenerator.getTestPostDocs().get(postDocIdx);

        for (var imageDocument : dataGenerator.getTestImageDocs()) {
            assertNotEquals(postDocument.getId(), imageDocument.getPostId());
        }

        PostDocumentWithImageData groundTruth = new PostDocumentWithImageData(
                postDocument.getId(),
                postDocument.getUserId(),
                postDocument.getHeadline(),
                postDocument.getText(),
                postDocument.getKarmaScore(),
                postDocument.getVisibility(),
                null
        );

        // when
        Optional<PostDocumentWithImageData> optionalResult = repository.findDocumentWithImageDataById(
                postDocument.getId());

        // then
        assertTrue(optionalResult.isPresent());
        assertEquals(groundTruth, optionalResult.get());

        // clear image docs
        imageRepository.deleteAll();
    }

    @Test
    void findDocumentWithImageData_PostDocumentDoesNotExists_EmptyOptionalIsReturned() {

        // save image docs
        imageRepository.insert(dataGenerator.getTestImageDocs());

        // when
        Optional<PostDocumentWithImageData> optionalResult = repository.findDocumentWithImageDataById(ObjectId.get());

        // then
        assertTrue(optionalResult.isEmpty());

        // clear image docs
        imageRepository.deleteAll();
    }

}