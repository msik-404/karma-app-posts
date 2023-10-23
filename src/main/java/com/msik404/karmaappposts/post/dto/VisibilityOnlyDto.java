package com.msik404.karmaappposts.post.dto;

import com.msik404.karmaappposts.post.Visibility;
import org.springframework.lang.NonNull;

public record VisibilityOnlyDto(@NonNull Visibility visibility) {
}
