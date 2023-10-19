package com.msik404.karmaappposts.post.dto;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrder;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPosition;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record FindParametersDto(int size, @NonNull PostDocScrollPositionConcrete position,
                                @NonNull Collection<Visibility> visibilities,
                                @NonNull PostDocRetrievalOrderStrategy order) {

    private static final int DEFAULT_SIZE = 100;
    private static final PostDocScrollPositionConcrete DEFAULT_POSITION = PostDocScrollPosition.initial();
    private static final Collection<Visibility> DEFAULT_VISIBILITY = List.of(Visibility.ACTIVE);
    private static final PostDocRetrievalOrderStrategy DEFAULT_ORDER = PostDocRetrievalOrder.desc();

    public FindParametersDto(
            @Nullable Integer size,
            @Nullable PostDocScrollPositionConcrete position,
            @Nullable Collection<Visibility> visibilities,
            @Nullable PostDocRetrievalOrderStrategy order) {

        this(
                size == null ? DEFAULT_SIZE : size,
                position == null ? DEFAULT_POSITION : position,
                visibilities == null ? DEFAULT_VISIBILITY : visibilities,
                order == null ? DEFAULT_ORDER : order
        );

    }
}
