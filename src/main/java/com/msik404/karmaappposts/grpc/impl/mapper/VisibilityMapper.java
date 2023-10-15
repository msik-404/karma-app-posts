package com.msik404.karmaappposts.grpc.impl.mapper;

import com.msik404.karmaappposts.grpc.PostVisibility;
import com.msik404.karmaappposts.grpc.impl.exception.UnsupportedVisibilityException;
import com.msik404.karmaappposts.post.Visibility;
import org.springframework.lang.NonNull;

public class VisibilityMapper {

    @NonNull
    public static Visibility map(@NonNull final PostVisibility visibility) throws UnsupportedVisibilityException {

        switch (visibility) {
            case VIS_ACTIVE -> {
                return Visibility.ACTIVE;
            }
            case VIS_HIDDEN -> {
                return Visibility.HIDDEN;
            }
            case VIS_DELETED -> {
                return Visibility.DELETED;
            }
            default -> throw new UnsupportedVisibilityException();
        }
    }

    @NonNull
    public static PostVisibility map(@NonNull final Visibility visibility) {

        switch (visibility) {
            case ACTIVE -> {
                return PostVisibility.VIS_ACTIVE;
            }
            case HIDDEN -> {
                return PostVisibility.VIS_HIDDEN;
            }
            case DELETED -> {
                return PostVisibility.VIS_DELETED;
            }
            default -> {
                return PostVisibility.UNRECOGNIZED;
            }
        }
    }
}
