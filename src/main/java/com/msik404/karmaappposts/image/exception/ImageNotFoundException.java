package com.msik404.karmaappposts.image.exception;

import com.msik404.karmaappposts.encoding.EncodableException;
import com.msik404.karmaappposts.encoding.ExceptionEncoder;
import com.msik404.karmaappposts.grpc.impl.exception.GrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class ImageNotFoundException extends RuntimeException implements EncodableException, GrpcStatusException {

    private final static String ERROR_MESSAGE = "Requested image was not found";

    public ImageNotFoundException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(ImageNotFoundException.class.getSimpleName(), ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public StatusRuntimeException asStatusRuntimeException() {
        return Status.NOT_FOUND
                .withDescription(getEncodedException())
                .asRuntimeException();
    }

}
