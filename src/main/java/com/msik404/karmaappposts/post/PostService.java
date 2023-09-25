package com.msik404.karmaappposts.post;

import java.util.Optional;

import com.msik404.karmaappposts.image.ImageService;
import com.msik404.karmaappposts.image.exception.FileProcessingException;
import com.msik404.karmaappposts.post.repository.PostRepository;
import com.msik404.karmaappposts.rating.RatingDocument;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
import com.msik404.karmaappposts.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final RatingRepository ratingRepository;

    private final ImageService imageService;

    public void create(
            @NonNull ObjectId userId,
            @NonNull String headline,
            @NonNull String text,
            @Nullable byte[] imageData) throws FileProcessingException {

        final PostDocument post = new PostDocument(userId, headline, text);
        postRepository.save(post);
        if (imageService != null) {
            imageService.save(post.getId(), imageData);
        }
    }

    public void rate(@NonNull ObjectId postId, @NonNull ObjectId userId, boolean isPositive) {

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

        postRepository.findAndIncrementKarmaScoreById(postId, delta);
    }

    public void unrate(@NonNull ObjectId postId, @NonNull ObjectId userId) {

        final Optional<IdAndIsPositiveOnlyDto> optionalDoc = ratingRepository.findByPostIdAndUserId(postId, userId);

        if (optionalDoc.isEmpty()) {
            // desired effect is already in place.
            return;
        }

        final var ratingDoc = optionalDoc.get();

        int delta = ratingDoc.isPositive() ? -1 : 1;

        postRepository.findAndIncrementKarmaScoreById(postId, delta);
        ratingRepository.deleteById(ratingDoc.id());
    }

}
