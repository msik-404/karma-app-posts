package com.msik404.karmaappposts.grpc.impl.exception;

import com.msik404.karmaappposts.encoding.EncodableException;
import com.msik404.karmaappposts.encoding.ExceptionEncoder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class UnsupportedVisibilityException extends RuntimeException implements EncodableException, GrpcStatusException {

    private final static String Id = "UnsupportedVisibility";
    private final static String ERROR_MESSAGE = "Unsupported visibility provided.";

    public UnsupportedVisibilityException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(getExceptionId(), ERROR_MESSAGE);
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
