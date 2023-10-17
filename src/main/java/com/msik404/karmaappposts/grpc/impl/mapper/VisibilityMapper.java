package com.msik404.karmaappposts.grpc.impl.mapper;

import com.msik404.karmaappposts.grpc.PostVisibility;
import com.msik404.karmaappposts.grpc.impl.exception.UnsupportedVisibilityException;
import com.msik404.karmaappposts.post.Visibility;
import org.springframework.lang.NonNull;

public class VisibilityMapper {

    @NonNull
    public static Visibility map(@NonNull final PostVisibility visibility) throws UnsupportedVisibilityException {

        return switch (visibility) {
            case VIS_ACTIVE -> Visibility.ACTIVE;
            case VIS_HIDDEN -> Visibility.HIDDEN;
            case VIS_DELETED -> Visibility.DELETED;
            default -> throw new UnsupportedVisibilityException();
        };
    }

    @NonNull
    public static PostVisibility map(@NonNull final Visibility visibility) {

        return switch (visibility) {
            case ACTIVE -> PostVisibility.VIS_ACTIVE;
            case HIDDEN -> PostVisibility.VIS_HIDDEN;
            case DELETED -> PostVisibility.VIS_DELETED;
            default -> PostVisibility.UNRECOGNIZED;
        };
    }
}
