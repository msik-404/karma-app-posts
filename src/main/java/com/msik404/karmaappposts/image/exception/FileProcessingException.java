package com.msik404.karmaappposts.image.exception;

import com.msik404.karmaappposts.encoding.EncodableException;
import com.msik404.karmaappposts.encoding.ExceptionEncoder;
import com.msik404.karmaappposts.grpc.impl.exception.GrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class FileProcessingException extends RuntimeException implements EncodableException, GrpcStatusException {

    private final static String ERROR_MESSAGE = "File could not be processed for some reason";

    public FileProcessingException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(FileProcessingException.class.getSimpleName(), ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public StatusRuntimeException asStatusRuntimeException() {
        return Status.INTERNAL
                .withDescription(getEncodedException())
                .asRuntimeException();
    }

}
