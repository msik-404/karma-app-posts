package com.msik404.karmaappposts.encoding;

import org.springframework.lang.NonNull;

public class ExceptionEncoder {

    private final static char SEPARATOR_CHAR = ';';

    @NonNull
    public static String encode(@NonNull final String exceptionIdString, @NonNull final String errorMessage) {
        return String.format("%s%c%s", exceptionIdString, SEPARATOR_CHAR, errorMessage);
    }

}
