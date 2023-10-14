package com.msik404.karmaappposts.image.exception;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException() {
        super("Requested image was not found");
    }
}
