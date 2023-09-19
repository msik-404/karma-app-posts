package com.msik404.karmaappposts;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
