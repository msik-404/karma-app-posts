package com.msik404.karmaappposts.grpc.impl;

import java.util.List;

import build.buf.protovalidate.ValidationResult;
import build.buf.protovalidate.Validator;
import build.buf.protovalidate.exceptions.ValidationException;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import com.msik404.karmaappposts.grpc.*;
import com.msik404.karmaappposts.grpc.impl.dto.PostRatingsRequestDto;
import com.msik404.karmaappposts.grpc.impl.dto.PostRatingsWithCreatorIdRequestDto;
import com.msik404.karmaappposts.grpc.impl.dto.PostsRequestDto;
import com.msik404.karmaappposts.grpc.impl.dto.PostsWithCreatorIdRequestDto;
import com.msik404.karmaappposts.grpc.impl.exception.UnsupportedVisibilityException;
import com.msik404.karmaappposts.grpc.impl.mapper.DocToGrpcMapper;
import com.msik404.karmaappposts.grpc.impl.mapper.VisibilityMapper;
import com.msik404.karmaappposts.image.ImageService;
import com.msik404.karmaappposts.image.exception.FileProcessingException;
import com.msik404.karmaappposts.image.exception.ImageNotFoundException;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.PostService;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.dto.PostDocumentWithImageData;
import com.msik404.karmaappposts.post.exception.PostNotFoundException;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
import com.msik404.karmaappposts.rating.exception.RatingNotFoundException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostsGrpcImpl extends PostsGrpc.PostsImplBase {

    private final PostService postService;
    private final PostRepositoryGrpcHandler postRepositoryHandler;
    private final RatingRepositoryGrpcHandler ratingRepositoryHandler;
    private final ImageService imageService;

    private static <T> boolean validate(
            @NonNull final Message request,
            @NonNull final StreamObserver<T> responseObserver) {

        final Validator validator = new Validator();
        try {
            final ValidationResult result = validator.validate(request);
            // Check if there are any validation violations
            if (!result.isSuccess()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription(result.toString())
                        .asRuntimeException()
                );
                return false;
            }
        } catch (ValidationException ex) {
            // Catch and print any ValidationExceptions thrown during the validation process
            final String errMessage = ex.getMessage();
            System.out.println("Validation failed: " + errMessage);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(errMessage)
                    .asRuntimeException()
            );
            return false;
        }
        return true;
    }

    @Override
    public void createPost(
            @NonNull final CreatePostRequest request,
            @NonNull final StreamObserver<Empty> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            postService.create(
                    new ObjectId(request.getUserId().getHexString()),
                    request.hasHeadline() ? request.getHeadline() : null,
                    request.hasText() ? request.getText() : null,
                    request.hasImageData() ? request.getImageData().toByteArray() : null
            );

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (FileProcessingException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void ratePost(
            @NonNull final RatePostRequest request,
            @NonNull final StreamObserver<ChangedRatingResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final int delta = postService.rate(
                    new ObjectId(request.getPostId().getHexString()),
                    new ObjectId(request.getUserId().getHexString()),
                    request.getIsPositive()
            );

            final var response = ChangedRatingResponse.newBuilder()
                    .setDelta(delta)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (PostNotFoundException | RatingNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void unratePost(
            @NonNull final UnratePostRequest request,
            @NonNull final StreamObserver<ChangedRatingResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final int delta = postService.unrate(
                    new ObjectId(request.getPostId().getHexString()),
                    new ObjectId(request.getUserId().getHexString())
            );

            final var response = ChangedRatingResponse.newBuilder()
                    .setDelta(delta)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (PostNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void changePostVisibility(
            @NonNull final ChangePostVisibilityRequest request,
            @NonNull final StreamObserver<Empty> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final Visibility visibility = VisibilityMapper.map(request.getVisibility());
            postService.findAndSetVisibilityById(new ObjectId(request.getPostId().getHexString()), visibility);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (UnsupportedVisibilityException | PostNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findPosts(
            @NonNull final PostsRequest request,
            @NonNull final StreamObserver<PostsResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final var mappedRequest = new PostsRequestDto(request);

            final List<PostDocument> posts = postRepositoryHandler.findFirstN(mappedRequest);

            final var responseBuilder = PostsResponse.newBuilder();

            for(PostDocument post : posts) {
                responseBuilder.addPosts(DocToGrpcMapper.map(post));
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findPostsWithCreatorId(
            @NonNull final PostsWithCreatorIdRequest request,
            @NonNull final StreamObserver<PostsResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final var mappedRequest = new PostsWithCreatorIdRequestDto(request);

            final List<PostDocument> posts = postRepositoryHandler.findFirstN(mappedRequest);

            final var responseBuilder = PostsResponse.newBuilder();

            for (PostDocument post : posts) {
                responseBuilder.addPosts(DocToGrpcMapper.map(post));
            }

            responseObserver.onNext(responseBuilder.build());

        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findImage(
            @NonNull final ImageRequest request,
            @NonNull final StreamObserver<ImageResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final byte[] imageData = imageService.findImageByPostId(new ObjectId(request.getPostId().getHexString()));

            final var response = ImageResponse.newBuilder()
                    .setImageData(ByteString.copyFrom(imageData))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (ImageNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @NonNull
    private static PostRatingsResponse buildPostRatingsResponse(
            @NonNull final List<IdAndIsPositiveOnlyDto> ratings) {

        final var responseBuilder = PostRatingsResponse.newBuilder();

        for (IdAndIsPositiveOnlyDto rating: ratings) {
            responseBuilder.addPostRatings(DocToGrpcMapper.map(rating));
        }

        return responseBuilder.build();
    }

    @Override
    public void findPostRatings(
            @NonNull final PostRatingsRequest request,
            @NonNull final StreamObserver<PostRatingsResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final var mappedRequest = new PostRatingsRequestDto(request);

            final List<IdAndIsPositiveOnlyDto> ratings = ratingRepositoryHandler.findFirstN(mappedRequest);

            responseObserver.onNext(buildPostRatingsResponse(ratings));
            responseObserver.onCompleted();

        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findPostRatingsWithCreatorId(
            @NonNull final PostRatingsWithCreatorIdRequest request,
            @NonNull final StreamObserver<PostRatingsResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final var mappedRequest = new PostRatingsWithCreatorIdRequestDto(request);

            final List<IdAndIsPositiveOnlyDto> ratings = ratingRepositoryHandler.findFirstN(mappedRequest);

            responseObserver.onNext(buildPostRatingsResponse(ratings));
            responseObserver.onCompleted();

        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findPostCreatorId(
            @NonNull final PostCreatorIdRequest request,
            @NonNull final StreamObserver<PostCreatorIdResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final ObjectId creatorId = postService.findPostCreatorId(new ObjectId(request.getPostId().getHexString()));

            final PostCreatorIdResponse response = PostCreatorIdResponse.newBuilder()
                    .setUserId(MongoObjectId.newBuilder().setHexString(creatorId.toHexString()).build())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (PostNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findPostWithImageData(
            @NonNull final PostRequest request,
            @NonNull final StreamObserver<PostWithImageData> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final PostDocumentWithImageData post = postService.findPostWithImageData(
                    new ObjectId(request.getPostId().getHexString()));

            responseObserver.onNext(DocToGrpcMapper.map(post));
            responseObserver.onCompleted();

        } catch (PostNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

}
