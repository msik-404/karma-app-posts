package com.msik404.karmaappposts.grpc.impl.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class FailedValidationException extends EncodableGrpcStatusException {

    private static final String Id = "FailedValidation";

    public FailedValidationException(String errorMessage) {
        super(errorMessage);
    }

    @NonNull
    @Override
    public String getExceptionId() {
        return Id;
    }

    @NonNull
    @Override
    public StatusRuntimeException asStatusRuntimeException() {
        return Status.INVALID_ARGUMENT
                .withDescription(getEncodedException())
                .asRuntimeException();
    }
}
