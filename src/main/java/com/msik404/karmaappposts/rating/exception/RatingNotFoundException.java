package com.msik404.karmaappposts.rating.exception;

import com.msik404.karmaappposts.encoding.EncodableException;
import com.msik404.karmaappposts.encoding.ExceptionEncoder;
import com.msik404.karmaappposts.grpc.impl.exception.GrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class RatingNotFoundException extends RuntimeException implements EncodableException, GrpcStatusException {

    private static final String ERROR_MESSAGE = "Rating with provided post_id and user_id was not found.";

    public RatingNotFoundException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(RatingNotFoundException.class.getSimpleName(), ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public StatusRuntimeException asStatusRuntimeException() {
        return Status.NOT_FOUND
                .withDescription(getEncodedException())
                .asRuntimeException();
    }

}
