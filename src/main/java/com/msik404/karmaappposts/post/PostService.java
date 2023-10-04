package com.msik404.karmaappposts.post;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.msik404.karmaappposts.image.ImageService;
import com.msik404.karmaappposts.image.exception.FileProcessingException;
import com.msik404.karmaappposts.post.dto.FindParametersDto;
import com.msik404.karmaappposts.post.exception.PostNotFoundException;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import com.msik404.karmaappposts.post.repository.PostRepository;
import com.msik404.karmaappposts.rating.RatingDocument;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
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

    private final ImageService imageService;

    public void findAndIncrementKarmaScoreById(@NonNull ObjectId id, int increment) throws PostNotFoundException {

        final long affectedDocs = postRepository.findAndIncrementKarmaScoreById(id, increment);
        if (affectedDocs == 0) {
            throw new PostNotFoundException();
        }
    }

    public void findAndSetVisibilityById(
            @NonNull ObjectId id,
            @NonNull Visibility visibility) throws PostNotFoundException {

        final long affectedDocs = postRepository.findAndSetVisibilityById(id, visibility);
        if (affectedDocs == 0) {
            throw new PostNotFoundException();
        }
    }

    @Transactional
    public void create(
            @NonNull ObjectId userId,
            @Nullable String headline,
            @Nullable String text,
            @Nullable byte[] imageData) throws FileProcessingException {

        final PostDocument post = new PostDocument(userId, headline, text);
        postRepository.save(post);
        if (imageData != null) {
            imageService.save(post.getId(), imageData);
        }
    }

    @Transactional
    public void rate(
            @NonNull ObjectId postId,
            @NonNull ObjectId userId,
            boolean isPositive) throws PostNotFoundException {

        final Optional<IdAndIsPositiveOnlyDto> optionalDoc = ratingRepository.findByPostIdAndUserId(postId, userId);

        int delta;

        if (optionalDoc.isEmpty()) {
            delta = isPositive ? 1 : -1;
            ratingRepository.save(new RatingDocument(postId, userId, isPositive));
        } else {
            final var ratingDoc = optionalDoc.get();
            if (ratingDoc.isPositive() == isPositive) {
                // desired effect is already in place.
                return;
            }
            delta = isPositive ? 2 : -2;
            ratingRepository.findAndSetIsPositiveById(ratingDoc.id(), isPositive);
        }

        findAndIncrementKarmaScoreById(postId, delta);
    }

    @Transactional
    public void unrate(@NonNull ObjectId postId, @NonNull ObjectId userId) throws PostNotFoundException {

        final Optional<IdAndIsPositiveOnlyDto> optionalDoc = ratingRepository.findByPostIdAndUserId(postId, userId);

        if (optionalDoc.isEmpty()) {
            // desired effect is already in place.
            return;
        }

        final var ratingDoc = optionalDoc.get();

        int delta = ratingDoc.isPositive() ? -1 : 1;

        findAndIncrementKarmaScoreById(postId, delta);
        ratingRepository.deleteById(ratingDoc.id());
    }

    public List<PostDocument> findFirstN(
            @Nullable Integer size,
            @Nullable PostDocScrollPositionConcrete position,
            @Nullable Collection<Visibility> visibilities,
            @Nullable PostDocRetrievalOrderStrategy order) {

        final var params = new FindParametersDto(size, position, visibilities, order);

        return postRepository.findFirstN(
                params.getSize(),
                params.getPosition(),
                params.getVisibilities(),
                params.getOrder()
        );
    }

    public List<PostDocument> findFirstN(
            @Nullable Integer size,
            @NonNull ObjectId creatorId,
            @Nullable PostDocScrollPositionConcrete position,
            @Nullable Collection<Visibility> visibilities,
            @Nullable PostDocRetrievalOrderStrategy order) {

        final var params = new FindParametersDto(size, position, visibilities, order);

        return postRepository.findFirstN(
                params.getSize(),
                creatorId,
                params.getPosition(),
                params.getVisibilities(),
                params.getOrder()
        );
    }

}
