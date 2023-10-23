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
import com.msik404.karmaappposts.grpc.impl.exception.FailedValidationException;
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
            @NonNull Message request,
            @NonNull StreamObserver<T> responseObserver) {

        Validator validator = new Validator();
        try {
            ValidationResult result = validator.validate(request);
            // Check if there are any validation violations

            if (!result.isSuccess()) {
                var exception = new FailedValidationException(result.toString());
                responseObserver.onError(exception.asStatusRuntimeException());
                return false;
            }
        } catch (ValidationException ex) {
            // Catch and print any ValidationExceptions thrown during the validation process
            String errMessage = ex.getMessage();
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
            CreatePostRequest request,
            StreamObserver<Empty> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
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
            RatePostRequest request,
            StreamObserver<ChangedRatingResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            int delta = postService.rate(
                    new ObjectId(request.getPostId().getHexString()),
                    new ObjectId(request.getUserId().getHexString()),
                    request.getIsPositive()
            );

            var response = ChangedRatingResponse.newBuilder()
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
            UnratePostRequest request,
            StreamObserver<ChangedRatingResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            int delta = postService.unrate(
                    new ObjectId(request.getPostId().getHexString()),
                    new ObjectId(request.getUserId().getHexString())
            );

            var response = ChangedRatingResponse.newBuilder()
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
            ChangePostVisibilityRequest request,
            StreamObserver<Empty> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            Visibility visibility = VisibilityMapper.map(request.getVisibility());
            postService.findAndSetVisibilityById(new ObjectId(request.getPostId().getHexString()), visibility);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (UnsupportedVisibilityException | PostNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findPosts(
            PostsRequest request,
            StreamObserver<PostsResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            var mappedRequest = new PostsRequestDto(request);

            List<PostDocument> posts = postRepositoryHandler.findFirstN(mappedRequest);

            var responseBuilder = PostsResponse.newBuilder();

            for (PostDocument post : posts) {
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
            PostsWithCreatorIdRequest request,
            StreamObserver<PostsResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            var mappedRequest = new PostsWithCreatorIdRequestDto(request);

            List<PostDocument> posts = postRepositoryHandler.findFirstN(mappedRequest);

            var responseBuilder = PostsResponse.newBuilder();

            for (PostDocument post : posts) {
                responseBuilder.addPosts(DocToGrpcMapper.map(post));
            }
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findImage(
            ImageRequest request,
            StreamObserver<ImageResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            byte[] imageData = imageService.findImageByPostId(new ObjectId(request.getPostId().getHexString()));

            var response = ImageResponse.newBuilder()
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
            @NonNull List<IdAndIsPositiveOnlyDto> ratings) {

        var responseBuilder = PostRatingsResponse.newBuilder();

        for (IdAndIsPositiveOnlyDto rating : ratings) {
            responseBuilder.addPostRatings(DocToGrpcMapper.map(rating));
        }

        return responseBuilder.build();
    }

    @Override
    public void findPostRatings(
            PostRatingsRequest request,
            StreamObserver<PostRatingsResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            var mappedRequest = new PostRatingsRequestDto(request);

            List<IdAndIsPositiveOnlyDto> ratings = ratingRepositoryHandler.findFirstN(mappedRequest);

            responseObserver.onNext(buildPostRatingsResponse(ratings));
            responseObserver.onCompleted();

        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findPostRatingsWithCreatorId(
            PostRatingsWithCreatorIdRequest request,
            StreamObserver<PostRatingsResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            var mappedRequest = new PostRatingsWithCreatorIdRequestDto(request);

            List<IdAndIsPositiveOnlyDto> ratings = ratingRepositoryHandler.findFirstN(mappedRequest);

            responseObserver.onNext(buildPostRatingsResponse(ratings));
            responseObserver.onCompleted();

        } catch (UnsupportedVisibilityException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findPostCreatorId(
            PostCreatorIdRequest request,
            StreamObserver<PostCreatorIdResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            ObjectId creatorId = postService.findPostCreatorId(new ObjectId(request.getPostId().getHexString()));

            PostCreatorIdResponse response = PostCreatorIdResponse.newBuilder()
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
            PostRequest request,
            StreamObserver<PostWithImageData> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            PostDocumentWithImageData post = postService.findPostWithImageData(
                    new ObjectId(request.getPostId().getHexString()));

            responseObserver.onNext(DocToGrpcMapper.map(post));
            responseObserver.onCompleted();

        } catch (PostNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findPostVisibility(
            MongoObjectId request,
            StreamObserver<PostVisibilityResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            Visibility visibility = postService.findVisibility(new ObjectId(request.getHexString()));

            responseObserver.onNext(PostVisibilityResponse.newBuilder()
                    .setVisibility(VisibilityMapper.map(visibility))
                    .build()
            );
            responseObserver.onCompleted();

        } catch (PostNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

}
