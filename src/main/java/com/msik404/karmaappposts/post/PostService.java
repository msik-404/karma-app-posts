package com.msik404.karmaappposts.post;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappposts.image.ImageService;
import com.msik404.karmaappposts.image.exception.FileProcessingException;
import com.msik404.karmaappposts.post.dto.FindParametersDto;
import com.msik404.karmaappposts.post.dto.PostDocumentWithImageData;
import com.msik404.karmaappposts.post.dto.UserIdOnlyDto;
import com.msik404.karmaappposts.post.dto.VisibilityOnlyDto;
import com.msik404.karmaappposts.post.exception.PostNotFoundException;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import com.msik404.karmaappposts.post.repository.PostRepository;
import com.msik404.karmaappposts.rating.RatingDocument;
import com.msik404.karmaappposts.rating.RatingService;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
import com.msik404.karmaappposts.rating.exception.RatingNotFoundException;
import com.msik404.karmaappposts.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final RatingRepository ratingRepository;

    private final RatingService ratingService;
    private final ImageService imageService;

    public void findAndIncrementKarmaScoreById(
            @NonNull ObjectId id,
            int increment) throws PostNotFoundException {

        long affectedDocs = postRepository.findAndIncrementKarmaScoreById(id, increment);
        if (affectedDocs == 0) {
            throw new PostNotFoundException();
        }
    }

    public void findAndSetVisibilityById(
            @NonNull ObjectId id,
            @NonNull Visibility visibility) throws PostNotFoundException {

        // Ignores case in which requested visibility is already in place.
        UpdateResult result = postRepository.findAndSetVisibilityById(id, visibility);
        if (result.getMatchedCount() == 0) {
            throw new PostNotFoundException();
        }
    }

    @Transactional
    public void create(
            @NonNull ObjectId userId,
            @Nullable String headline,
            @Nullable String text,
            @Nullable byte[] imageData) throws FileProcessingException {

        PostDocument post = new PostDocument(userId, headline, text);
        postRepository.save(post);
        if (imageData != null) {
            imageService.save(post.getId(), imageData);
        }
    }

    @Transactional
    public int rate(
            @NonNull ObjectId postId,
            @NonNull ObjectId userId,
            boolean isPositive) throws PostNotFoundException, RatingNotFoundException {

        Optional<IdAndIsPositiveOnlyDto> optionalDoc = ratingRepository.findByPostIdAndUserId(postId, userId);

        int delta;

        if (optionalDoc.isEmpty()) {
            delta = isPositive ? 1 : -1;
            ratingRepository.save(new RatingDocument(postId, userId, isPositive));
        } else {
            var ratingDoc = optionalDoc.get();
            if (ratingDoc.isPositive() == isPositive) {
                // desired effect is already in place.
                return 0;
            }
            delta = isPositive ? 2 : -2;
            ratingService.findAndSetIsPositiveById(ratingDoc.id(), isPositive);
        }

        findAndIncrementKarmaScoreById(postId, delta);

        return delta;
    }

    @Transactional
    public int unrate(
            @NonNull ObjectId postId,
            @NonNull ObjectId userId) throws PostNotFoundException {

        Optional<IdAndIsPositiveOnlyDto> optionalDoc = ratingRepository.findByPostIdAndUserId(postId, userId);

        if (optionalDoc.isEmpty()) {
            // desired effect is already in place.
            return 0;
        }

        var ratingDoc = optionalDoc.get();

        int delta = ratingDoc.isPositive() ? -1 : 1;

        findAndIncrementKarmaScoreById(postId, delta);
        ratingRepository.deleteById(ratingDoc.id());

        return delta;
    }

    @NonNull
    public List<PostDocument> findFirstN(
            @Nullable Integer size,
            @Nullable PostDocScrollPositionConcrete position,
            @Nullable Collection<Visibility> visibilities,
            @Nullable PostDocRetrievalOrderStrategy order) {

        var params = new FindParametersDto(size, position, visibilities, order);

        return postRepository.findFirstN(
                params.size(),
                params.position(),
                params.visibilities(),
                params.order()
        );
    }

    @NonNull
    public List<PostDocument> findFirstN(
            @Nullable Integer size,
            @NonNull ObjectId creatorId,
            @Nullable PostDocScrollPositionConcrete position,
            @Nullable Collection<Visibility> visibilities,
            @Nullable PostDocRetrievalOrderStrategy order) {

        var params = new FindParametersDto(size, position, visibilities, order);

        return postRepository.findFirstN(
                params.size(),
                creatorId,
                params.position(),
                params.visibilities(),
                params.order()
        );
    }

    @NonNull
    public PostDocumentWithImageData findPostWithImageData(@NonNull ObjectId postId) throws PostNotFoundException {
        return postRepository.findDocumentWithImageData(postId).orElseThrow(PostNotFoundException::new);
    }

    @NonNull
    public ObjectId findPostCreatorId(@NonNull ObjectId postId) throws PostNotFoundException {

        Optional<UserIdOnlyDto> optionalCreatorId = postRepository.findUserIdById(postId);

        if (optionalCreatorId.isEmpty()) {
            throw new PostNotFoundException();
        }

        return optionalCreatorId.get().userId();
    }

    @NonNull
    public Visibility findVisibility(@NonNull ObjectId postId) throws PostNotFoundException {

        Optional<VisibilityOnlyDto> optionalVisibility = postRepository.findVisibilityById(postId);

        if (optionalVisibility.isEmpty()) {
            throw new PostNotFoundException();
        }

        return optionalVisibility.get().visibility();
    }

}
