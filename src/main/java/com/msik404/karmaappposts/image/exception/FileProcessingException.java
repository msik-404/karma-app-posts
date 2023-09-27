package com.msik404.karmaappposts.image.exception;

public class FileProcessingException extends RuntimeException {

    public FileProcessingException() {
        super("File could not be processed for some reason");
    }

}
