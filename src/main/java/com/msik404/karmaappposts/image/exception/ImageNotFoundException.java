package com.msik404.karmaappposts.image.exception;

import com.msik404.karmaappposts.grpc.impl.exception.EncodableGrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class ImageNotFoundException extends EncodableGrpcStatusException {

    private final static String Id = "ImageNotFound";
    private final static String ERROR_MESSAGE = "Requested image was not found";

    public ImageNotFoundException() {
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
