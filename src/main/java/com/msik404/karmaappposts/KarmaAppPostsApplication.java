package com.msik404.karmaappposts;

import com.msik404.karmaappposts.post.PostDocument;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class KarmaAppPostsApplication {

    public static void main(String[] args) {
        SpringApplication.run(KarmaAppPostsApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return (args) -> {
            System.out.println("GRPC SERVER WILL RUN HERE");
        };
    }

}
