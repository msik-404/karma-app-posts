package com.msik404.karmaappposts.image.exception;

import com.msik404.karmaappposts.grpc.impl.exception.EncodableGrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class FileProcessingException extends EncodableGrpcStatusException {

    private final static String Id = "FileProcessing";
    private final static String ERROR_MESSAGE = "File could not be processed for some reason";

    public FileProcessingException() {
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
        return Status.INTERNAL
                .withDescription(getEncodedException())
                .asRuntimeException();
    }

}
