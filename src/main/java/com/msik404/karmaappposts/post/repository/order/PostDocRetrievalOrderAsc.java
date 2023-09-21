package com.msik404.karmaappposts.post.repository.order;

import org.springframework.data.domain.Sort;

public class PostDocRetrievalOrderAsc implements PostDocRetrievalOrderStrategy {

    private static final Sort ASC_ORDER = Sort.by("karmaScore").ascending()
            .and(Sort.by("_id").descending());

    @Override
    public Sort getOrderStrategy() {
        return ASC_ORDER;
    }

}
