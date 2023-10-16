package com.msik404.karmaappposts.post.exception;

import com.msik404.karmaappposts.encoding.EncodableException;
import com.msik404.karmaappposts.encoding.ExceptionEncoder;
import com.msik404.karmaappposts.grpc.impl.exception.GrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class PostNotFoundException extends RuntimeException implements EncodableException, GrpcStatusException {

    private static final String ERROR_MESSAGE = "Post with provided id was not found.";

    public PostNotFoundException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(PostNotFoundException.class.getSimpleName(), ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public StatusRuntimeException asStatusRuntimeException() {
        return Status.NOT_FOUND
                .withDescription(getEncodedException())
                .asRuntimeException();
    }

}
