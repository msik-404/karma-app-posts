package com.msik404.karmaappposts.grpc.impl.exception;

import com.msik404.karmaappposts.encoding.EncodableException;
import com.msik404.karmaappposts.encoding.ExceptionEncoder;
import org.springframework.lang.NonNull;

public abstract class EncodableGrpcStatusException extends RuntimeException implements EncodableException, GrpcStatusException {

    public EncodableGrpcStatusException(@NonNull final String errorMessage) {
        super(errorMessage);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(getExceptionId(), getMessage());
    }

}
