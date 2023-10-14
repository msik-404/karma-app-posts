package com.msik404.karmaappposts.post.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.springframework.lang.NonNull;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
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

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    @DynamicPropertySource
    private static void registerRedisProperties(@NonNull DynamicPropertyRegistry registry) {

        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
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
        final int lastPostDocIdx = 0;
        final ObjectId creatorId = TestingDataGenerator.TEST_USER_IDS.get(0);

        final int size = 15;
        final Set<Visibility> visibilities = Set.of(Visibility.ACTIVE, Visibility.HIDDEN);
        List<PostDocument> groundTruth = dataGenerator.getTestPostDocs().stream()
                .filter(post -> visibilities.contains(post.getVisibility()) && post.getUserId().equals(creatorId))
                .toList();

        final PostDocument lastPostDoc = groundTruth.get(lastPostDocIdx);
        groundTruth = groundTruth.subList(1, 3);

        // when
        final List<PostDocument> results = repository.findFirstN(
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
        final PostDocument post = dataGenerator.getTestPostDocs().get(0);
        final ObjectId postId = post.getId();

        // when
        final Optional<UserIdOnlyDto> optionalObjectId = repository.findByPostId(postId);

        // then
        assertTrue(optionalObjectId.isPresent());
        assertEquals(post.getUserId(), optionalObjectId.get().userId());
    }

    @Test
    void findCreatorIdByPostId_PostDoesNotExist_CreatorIsNotFound() {

        // given
        final ObjectId postId = ObjectId.get();

        // when
        final Optional<UserIdOnlyDto> optionalObjectId = repository.findByPostId(postId);

        // then
        assertTrue(optionalObjectId.isEmpty());
    }

    @Test
    void findDocumentWithImageData_PostDocumentAndImageDocumentExist_PostDocumentWithImageDataIsReturned() {

        // save image docs
        imageRepository.insert(dataGenerator.getTestImageDocs());

        // given
        final int postDocIdx = 0;
        final PostDocument postDocument = dataGenerator.getTestPostDocs().get(postDocIdx);

        final int imageDocIdx = 0;
        final ImageDocument imageDocument = dataGenerator.getTestImageDocs().get(imageDocIdx);

        assertEquals(postDocument.getId(), imageDocument.getPostId());

        final PostDocumentWithImageData groundTruth = new PostDocumentWithImageData(
                postDocument.getId(),
                postDocument.getUserId(),
                postDocument.getHeadline(),
                postDocument.getText(),
                postDocument.getKarmaScore(),
                postDocument.getVisibility(),
                imageDocument.getImageData()
        );

        // when
        final Optional<PostDocumentWithImageData> optionalResult = repository.findDocumentWithImageData(
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
        final int postDocIdx = 4;
        final PostDocument postDocument = dataGenerator.getTestPostDocs().get(postDocIdx);

        for (var imageDocument : dataGenerator.getTestImageDocs()) {
            assertNotEquals(postDocument.getId(), imageDocument.getPostId());
        }

        final PostDocumentWithImageData groundTruth = new PostDocumentWithImageData(
                postDocument.getId(),
                postDocument.getUserId(),
                postDocument.getHeadline(),
                postDocument.getText(),
                postDocument.getKarmaScore(),
                postDocument.getVisibility(),
                null
        );

        // when
        final Optional<PostDocumentWithImageData> optionalResult = repository.findDocumentWithImageData(
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
        final Optional<PostDocumentWithImageData> optionalResult = repository.findDocumentWithImageData(ObjectId.get());

        // then
        assertTrue(optionalResult.isEmpty());

        // clear image docs
        imageRepository.deleteAll();
    }

}