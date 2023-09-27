package com.msik404.karmaappposts.post.exception;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException() {
        super("Post with provided id was not found.");
    }

}
