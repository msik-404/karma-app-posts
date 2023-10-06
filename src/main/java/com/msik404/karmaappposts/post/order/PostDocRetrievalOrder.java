package com.msik404.karmaappposts.post.order;

import org.springframework.lang.NonNull;

public class PostDocRetrievalOrder {

    @NonNull
    public static PostDocRetrievalOrderAsc asc() {
        return new PostDocRetrievalOrderAsc();
    }

    @NonNull
    public static PostDocRetrievalOrderDesc desc() {
        return new PostDocRetrievalOrderDesc();
    }

}
