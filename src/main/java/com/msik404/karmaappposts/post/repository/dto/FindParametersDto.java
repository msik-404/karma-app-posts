package com.msik404.karmaappposts.post.repository.dto;

import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrder;
import com.msik404.karmaappposts.post.repository.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPosition;
import com.msik404.karmaappposts.post.repository.position.PostDocScrollPositionConcrete;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class FindParametersDto {

    private static final int DEFAULT_SIZE = 100;
    private static final PostDocScrollPositionConcrete DEFAULT_POSITION = PostDocScrollPosition.initial();
    private static final Collection<Visibility> DEFAULT_VISIBILITY = List.of(Visibility.ACTIVE);
    private static final PostDocRetrievalOrderStrategy DEFAULT_ORDER = PostDocRetrievalOrder.desc();

    final int size;
    final PostDocScrollPositionConcrete position;
    final Collection<Visibility> visibilities;
    final PostDocRetrievalOrderStrategy order;

    public FindParametersDto(
            @Nullable Integer size,
            @Nullable PostDocScrollPositionConcrete position,
            @Nullable Collection<Visibility> visibilities,
            @Nullable PostDocRetrievalOrderStrategy order) {

        this.size = size == null ? DEFAULT_SIZE : size;
        this.position = position == null ? DEFAULT_POSITION : position;
        this.visibilities = visibilities == null ? DEFAULT_VISIBILITY : visibilities;
        this.order = order == null ? DEFAULT_ORDER : order;
    }
}
