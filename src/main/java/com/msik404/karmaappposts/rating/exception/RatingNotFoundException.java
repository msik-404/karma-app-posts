package com.msik404.karmaappposts.rating.exception;

import com.msik404.karmaappposts.grpc.impl.exception.EncodableGrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class RatingNotFoundException extends EncodableGrpcStatusException {

    private static final String Id = "RatingNotFound";
    private static final String ERROR_MESSAGE = "Rating with provided post_id and user_id was not found.";

    public RatingNotFoundException() {
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
