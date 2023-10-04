package com.msik404.karmaappposts.grpc.impl;

import java.util.List;

import com.msik404.karmaappposts.grpc.impl.dto.PostsRequestDto;
import com.msik404.karmaappposts.grpc.impl.dto.PostsWithCreatorIdRequestDto;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostRepositoryGrpcHandler {

    private final PostService postService;

    List<PostDocument> findFirstN(@NonNull PostsRequestDto request) {

        return postService.findFirstN(
                request.size(),
                request.position(),
                request.visibilities(),
                request.order()
        );
    }

    List<PostDocument> findFirstN(@NonNull PostsWithCreatorIdRequestDto request) {

        return postService.findFirstN(
                request.postsRequestDto().size(),
                request.creatorId(),
                request.postsRequestDto().position(),
                request.postsRequestDto().visibilities(),
                request.postsRequestDto().order()
        );
    }

}
