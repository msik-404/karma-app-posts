package com.msik404.karmaappposts.grpc.impl;

import java.util.List;
import java.util.Optional;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.msik404.karmaappposts.grpc.ChangePostVisibilityRequest;
import com.msik404.karmaappposts.grpc.CreatePostRequest;
import com.msik404.karmaappposts.grpc.ImageRequest;
import com.msik404.karmaappposts.grpc.ImageResponse;
import com.msik404.karmaappposts.grpc.PostRatingsRequest;
import com.msik404.karmaappposts.grpc.PostRatingsResponse;
import com.msik404.karmaappposts.grpc.PostsGrpc;
import com.msik404.karmaappposts.grpc.PostsRequest;
import com.msik404.karmaappposts.grpc.PostsResponse;
import com.msik404.karmaappposts.grpc.PostsWithCreatorIdRequest;
import com.msik404.karmaappposts.grpc.RatePostRequest;
import com.msik404.karmaappposts.grpc.UnratePostRequest;
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

    PostService postService;
    PostRepositoryGrpcHandler postRepositoryHandler;
    RatingRepositoryGrpcHandler ratingRepositoryHandler;
    ImageRepository imageRepository;

    @Override
    public void createPost(CreatePostRequest request, StreamObserver<Empty> responseObserver) {

        try {
            postService.create(
                    new ObjectId(request.getUserId()),
                    request.getHeadline(),
                    request.getText(),
                    request.getImageData().toByteArray()
            );
        } catch (FileProcessingException ex) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void ratePost(RatePostRequest request, StreamObserver<Empty> responseObserver) {

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

        try {
            Visibility visibility = VisibilityMapper.map(request.getVisibility());
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
    public void findPostsWithCreatorId(PostsWithCreatorIdRequest request, StreamObserver<PostsResponse> responseObserver) {

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
    public void findImage(ImageRequest request, StreamObserver<com.msik404.karmaappposts.grpc.ImageResponse> responseObserver) {

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
    public void findPostRatingsWithCreatorId(com.msik404.karmaappposts.grpc.PostRatingsWithCreatorIdRequest request, StreamObserver<PostRatingsResponse> responseObserver) {

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
