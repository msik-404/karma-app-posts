package com.msik404.karmaappposts.post.order;

import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

public interface PostDocRetrievalOrderStrategy {

    @NonNull
    Sort getOrderStrategy();

}
