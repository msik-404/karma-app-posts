package com.msik404.karmaappposts.encoding;

import org.springframework.lang.NonNull;

public class ExceptionEncoder {

    private final static char SEPARATOR_CHAR = ';';

    @NonNull
    public static String encode(@NonNull String simpleClassName, @NonNull String errorMessage) {
        return String.format("%s%c%s", simpleClassName, SEPARATOR_CHAR, errorMessage);
    }

}
