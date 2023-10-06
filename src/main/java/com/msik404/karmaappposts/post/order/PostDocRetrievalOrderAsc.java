package com.msik404.karmaappposts.post.order;

import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

public class PostDocRetrievalOrderAsc implements PostDocRetrievalOrderStrategy {

    private static final Sort ASC_ORDER = Sort.by("karmaScore").ascending()
            .and(Sort.by("_id").descending());

    @NonNull
    @Override
    public Sort getOrderStrategy() {
        return ASC_ORDER;
    }

}
