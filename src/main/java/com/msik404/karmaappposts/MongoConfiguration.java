package com.msik404.karmaappposts;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.rating.RatingDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

@Configuration
public class MongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient client) {
        return new SimpleMongoClientDatabaseFactory(client, databaseName);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {

        MongoTemplate mongoTemplate = new MongoTemplate(factory);

        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate
                .getConverter().getMappingContext();

        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        IndexOperations indexOps = mongoTemplate.indexOps(PostDocument.class);
        resolver.resolveIndexFor(PostDocument.class).forEach(indexOps::ensureIndex);

        indexOps = mongoTemplate.indexOps(RatingDocument.class);
        resolver.resolveIndexFor(RatingDocument.class).forEach(indexOps::ensureIndex);

        return mongoTemplate;
    }

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }

}
