package com.msik404.karmaappposts.rating.exception;

public class RatingNotFoundException extends RuntimeException {

    public RatingNotFoundException() {
        super("Rating with provided post_id and user_id was not found.");
    }

}
