package com.msik404.karmaappposts.post.order;

public class PostDocRetrievalOrder {

    public static PostDocRetrievalOrderAsc asc() {
        return new PostDocRetrievalOrderAsc();
    }

    public static PostDocRetrievalOrderDesc desc() {
        return new PostDocRetrievalOrderDesc();
    }

}
