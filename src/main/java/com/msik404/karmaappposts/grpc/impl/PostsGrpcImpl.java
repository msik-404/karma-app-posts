package com.msik404.karmaappposts.grpc.impl;

import java.util.List;
import java.util.Optional;

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
import com.msik404.karmaappposts.grpc.impl.mapper.DocToGrpcMapper;
import com.msik404.karmaappposts.grpc.impl.mapper.VisibilityMapper;
import com.msik404.karmaappposts.grpc.impl.mapper.exception.UnsupportedVisibilityException;
import com.msik404.karmaappposts.image.exception.FileProcessingException;
import com.msik404.karmaappposts.image.repository.ImageRepository;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.PostService;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.exception.PostNotFoundException;
import com.msik404.karmaappposts.rating.dto.PostIdAndIsPositiveOnlyDto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostsGrpcImpl extends PostsGrpc.PostsImplBase {

    private final PostService postService;
    private final PostRepositoryGrpcHandler postRepositoryHandler;
    private final RatingRepositoryGrpcHandler ratingRepositoryHandler;
    private final ImageRepository imageRepository;

    private static <T> boolean validate(Message request, StreamObserver<T> responseObserver) {

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
    public void createPost(CreatePostRequest request, StreamObserver<Empty> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            postService.create(
                    new ObjectId(request.getUserId()),
                    request.hasHeadline() ? request.getHeadline() : null,
                    request.hasText() ? request.getText() : null,
                    request.hasImageData() ? request.getImageData().toByteArray() : null
            );
        } catch (FileProcessingException ex) {
            final String errMessage = ex.getMessage();
            System.out.println(errMessage);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(errMessage)
                    .asRuntimeException()
            );
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void ratePost(RatePostRequest request, StreamObserver<Empty> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            postService.rate(
                    new ObjectId(request.getPostId()),
                    new ObjectId(request.getUserId()),
                    request.getIsPositive()
            );
        } catch (PostNotFoundException ex) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void unratePost(UnratePostRequest request, StreamObserver<Empty> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            postService.unrate(
                    new ObjectId(request.getPostId()),
                    new ObjectId(request.getUserId())
            );
        } catch (PostNotFoundException ex) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void changePostVisibility(ChangePostVisibilityRequest request, StreamObserver<Empty> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final Visibility visibility = VisibilityMapper.map(request.getVisibility());
            postService.findAndSetVisibilityById(new ObjectId(request.getPostId()), visibility);
        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        } catch (PostNotFoundException ex) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void findPosts(PostsRequest request, StreamObserver<PostsResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        PostsRequestDto mappedRequest;

        try {
            mappedRequest = new PostsRequestDto(request);
        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
            return;
        }

        final List<PostDocument> posts = postRepositoryHandler.findFirstN(mappedRequest);

        final PostsResponse response = PostsResponse.newBuilder()
                .addAllPosts(posts.stream().map(DocToGrpcMapper::map).toList())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findPostsWithCreatorId(
            PostsWithCreatorIdRequest request,
            StreamObserver<PostsResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        PostsWithCreatorIdRequestDto mappedRequest;

        try {
            mappedRequest = new PostsWithCreatorIdRequestDto(request);
        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
            return;
        }

        final List<PostDocument> posts = postRepositoryHandler.findFirstN(mappedRequest);

        final PostsResponse response = PostsResponse.newBuilder()
                .addAllPosts(posts.stream().map(DocToGrpcMapper::map).toList())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findImage(
            ImageRequest request,
            StreamObserver<ImageResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        Optional<Binary> optionalData = imageRepository.findImageDataById(new ObjectId(request.getPostId()));
        if (optionalData.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Image for post with provided post_id was not found")
                    .asRuntimeException()
            );
            return;
        }

        final var response = ImageResponse.newBuilder()
                .setImageData(ByteString.copyFrom(optionalData.get().getData()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findPostRatings(PostRatingsRequest request, StreamObserver<PostRatingsResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        PostRatingsRequestDto mappedRequest;

        try {
            mappedRequest = new PostRatingsRequestDto(request);
        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
            return;
        }

        final List<PostIdAndIsPositiveOnlyDto> ratings = ratingRepositoryHandler.findFirstN(mappedRequest);

        final var response = PostRatingsResponse
                .newBuilder()
                .addAllPostRatings(ratings.stream().map(DocToGrpcMapper::map).toList())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findPostRatingsWithCreatorId(
            PostRatingsWithCreatorIdRequest request,
            StreamObserver<PostRatingsResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        PostRatingsWithCreatorIdRequestDto mappedRequest;
        try {
            mappedRequest = new PostRatingsWithCreatorIdRequestDto(request);
        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
            return;
        }

        final List<PostIdAndIsPositiveOnlyDto> ratings = ratingRepositoryHandler.findFirstN(mappedRequest);

        final var response = PostRatingsResponse
                .newBuilder()
                .addAllPostRatings(ratings.stream().map(DocToGrpcMapper::map).toList())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
