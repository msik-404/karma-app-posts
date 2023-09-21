package com.msik404.karmaappposts.post.repository.order;

import org.springframework.data.domain.Sort;

public class PostDocRetrievalOrderDesc implements PostDocRetrievalOrderStrategy {

    private static final Sort DESC_ORDER = Sort.by("karmaScore").descending()
            .and(Sort.by("_id").ascending());

    public Sort getOrderStrategy() {
        return DESC_ORDER;
    }

}
