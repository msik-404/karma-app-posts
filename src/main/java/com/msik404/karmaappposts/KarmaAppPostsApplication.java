package com.msik404.karmaappposts;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.msik404.karmaappposts.grpc.impl.PostsGrpcImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class KarmaAppPostsApplication {

    @Value("${KarmaAppPosts.grpc.default.threadAmount}")
    private int threadPoolSize;

    @Value("${KarmaAppPosts.grpc.default.port}")
    private int defaultGrpcPort;

    private final PostsGrpcImpl grpcImpl;

    public static void main(String[] args) {
        SpringApplication.run(KarmaAppPostsApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return (args) -> {
            System.out.println("GRPC SERVER WILL RUN HERE");

            ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(threadPoolSize);

            Server server = ServerBuilder
                    .forPort(defaultGrpcPort)
                    .executor(threadPoolExecutor)
                    .addService(grpcImpl)
                    .build();

            server.start();
            server.awaitTermination();
        };
    }

}
