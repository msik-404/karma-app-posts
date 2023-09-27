package com.msik404.karmaappposts.grpc.impl;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.grpc.impl.dto.PostsRequestDto;
import com.msik404.karmaappposts.grpc.impl.dto.PostsWithCreatorIdRequestDto;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostRepositoryGrpcHandler {

    PostRepository postRepository;

    List<PostDocument> findFirstN(@NonNull PostsRequestDto request) {

        return postRepository.findFirstN(
                request.getSize(),
                request.getPosition(),
                request.getVisibilities(),
                request.getOrder()
        );
    }

    List<PostDocument> findFirstN(@NonNull PostsWithCreatorIdRequestDto request) {

        return postRepository.findFirstN(
                request.getSize(),
                request.getCreatorId(),
                request.getPosition(),
                request.getVisibilities(),
                request.getOrder()
        );
    }

}
