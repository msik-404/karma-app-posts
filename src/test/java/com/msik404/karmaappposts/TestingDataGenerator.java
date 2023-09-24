package com.msik404.karmaappposts;

import java.util.*;
import java.util.stream.Collectors;

import com.msik404.karmaappposts.image.ImageDocument;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.rating.RatingDocument;
import com.msik404.karmaappposts.rating.dto.RatingDocDto;
import lombok.Getter;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
public class TestingDataGenerator {

    public static final List<ObjectId> TEST_USER_IDS = getTestUserIds();

    private final List<PostDocument> testPostDocs;

    private final List<RatingDocument> testRatingDocs;

    private final List<ImageDocument> testImageDocs;

    public TestingDataGenerator() {

        this.testPostDocs = new ArrayList<>();
        this.testRatingDocs = new ArrayList<>();
        this.testImageDocs = new ArrayList<>();

        generateData(testPostDocs, testRatingDocs, testImageDocs);

        this.testPostDocs.sort(new PostDocComparator().reversed());
    }

    private static class PostDocComparator implements Comparator<PostDocument> {

        @Override
        public int compare(PostDocument first, PostDocument second) {

            if (first.getKarmaScore().equals(second.getKarmaScore())) {
                return -first.getId().compareTo(second.getId());
            }
            return first.getKarmaScore().compareTo(second.getKarmaScore());
        }

    }

    public static List<RatingDocDto> getRatings(
            @NonNull List<PostDocument> postDocs,
            @NonNull List<RatingDocument> ratingDocs,
            @NonNull ObjectId userId,
            @NonNull Collection<Visibility> visibilities) {

        final Map<ObjectId, Boolean> ratedDocsByUser = ratingDocs.stream()
                .filter(rating -> rating.getUserId().equals(userId))
                .collect(Collectors.toMap(RatingDocument::getPostId, RatingDocument::isPositive));

        return postDocs.stream()
                .filter(post -> visibilities.contains(post.getVisibility()))
                .map(post -> new RatingDocDto(
                        post.getId(), ratedDocsByUser.getOrDefault(post.getId(), null)))
                .toList();
    }

    private static List<ObjectId> getTestUserIds() {

        final int userAmount = 5;
        List<ObjectId> userIds = new ArrayList<>(userAmount);
        for (int i = 0; i < userAmount; i++) {
            userIds.add(ObjectId.get());
        }
        return userIds;
    }

    public static PostDocument getTestPostDoc(
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

    private static void ratePost(
            @NonNull List<RatingDocument> ratingsDocs,
            @NonNull ObjectId postId,
            long karmaScore) {

        assert karmaScore != 0;

        final boolean isPositive = karmaScore > 0;

        for (int i = 0; i < Math.abs(karmaScore); i++) {
            ratingsDocs.add(new RatingDocument(ObjectId.get(), postId, TEST_USER_IDS.get(i), isPositive));
        }
    }

    private static void generatePostData(
            @NonNull List<PostDocument> postDocs,
            @NonNull List<RatingDocument> ratingsDocs,
            @NonNull List<ImageDocument> imageDocs,
            int userIdx,
            long karmaScore,
            @NonNull Visibility visibility,
            @Nullable byte[] imageData) {

        assert karmaScore <= TEST_USER_IDS.size();

        final ObjectId userObjectId = TEST_USER_IDS.get(userIdx);
        final ObjectId postObjectId = ObjectId.get();
        postDocs.add(getTestPostDoc(postObjectId, userObjectId, karmaScore, visibility));

        if (karmaScore != 0) {
            ratePost(ratingsDocs, postObjectId, karmaScore);
        }

        if (imageData != null) {
            imageDocs.add(new ImageDocument(postObjectId, new Binary(imageData)));
        }
    }

    private static void generateData(
            @NonNull List<PostDocument> postDocs,
            @NonNull List<RatingDocument> ratingsDocs,
            @NonNull List<ImageDocument> imageDocs
    ) {

        interface DataGenerator {
            void generate(int userIdx, long karmaScore, Visibility visibility, byte[] imageData);
        }

        DataGenerator genData = (int userIdx, long karmaScore, Visibility visibility, byte[] imageData) -> {
            generatePostData(postDocs, ratingsDocs, imageDocs, userIdx, karmaScore, visibility, imageData);
        };

        genData.generate(0, 3, Visibility.ACTIVE, null);
        genData.generate(0, 1, Visibility.ACTIVE, null);
        genData.generate(0, -1, Visibility.HIDDEN, null);

        genData.generate(1, 5, Visibility.ACTIVE, "DATA".getBytes());
        genData.generate(1, 3, Visibility.ACTIVE, "DATA".getBytes());
        genData.generate(1, 0, Visibility.ACTIVE, null);
        genData.generate(1, -2, Visibility.DELETED, null);

        genData.generate(2, 5, Visibility.ACTIVE, "DATA".getBytes());
        genData.generate(2, 3, Visibility.ACTIVE, null);
        genData.generate(2, -5, Visibility.DELETED, null);

        genData.generate(3, 5, Visibility.ACTIVE, "DATA".getBytes());
        genData.generate(3, 3, Visibility.HIDDEN, null);
        genData.generate(3, -2, Visibility.DELETED, null);

        genData.generate(4, 4, Visibility.ACTIVE, "DATA".getBytes());
        genData.generate(4, 1, Visibility.HIDDEN, null);
        genData.generate(4, -3, Visibility.DELETED, null);

    }

}
