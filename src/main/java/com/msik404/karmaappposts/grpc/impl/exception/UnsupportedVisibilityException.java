package com.msik404.karmaappposts.grpc.impl.exception;

public class UnsupportedVisibilityException extends RuntimeException {

    public UnsupportedVisibilityException() {
        super("Unsupported visibility provided.");
    }

}
