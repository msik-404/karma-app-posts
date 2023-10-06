package com.msik404.karmaappposts.post.order;

import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

public class PostDocRetrievalOrderDesc implements PostDocRetrievalOrderStrategy {

    private static final Sort DESC_ORDER = Sort.by("karmaScore").descending()
            .and(Sort.by("_id").ascending());

    @NonNull
    public Sort getOrderStrategy() {
        return DESC_ORDER;
    }

}
