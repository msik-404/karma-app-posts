package com.msik404.karmaappposts.post.exception;

import com.msik404.karmaappposts.grpc.impl.exception.EncodableGrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class PostNotFoundException extends EncodableGrpcStatusException {

    private static final String Id = "PostNotFound";
    private static final String ERROR_MESSAGE = "Post with provided id was not found.";

    public PostNotFoundException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getExceptionId() {
        return Id;
    }

    @NonNull
    @Override
    public StatusRuntimeException asStatusRuntimeException() {
        return Status.NOT_FOUND
                .withDescription(getEncodedException())
                .asRuntimeException();
    }

}
