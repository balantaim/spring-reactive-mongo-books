package com.martinatanasov.reactivemongo.config;

//import com.mongodb.MongoClientSettings;
//import com.mongodb.MongoCredential;
//import com.mongodb.ServerAddress;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

//import static java.util.Collections.singletonList;

//Disable @Configuration in order to externalize the properties in application.properties
//@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Bean
    public MongoClient mongoClient(){
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Override
    protected String getDatabaseName() {
        return "spring-reactive-demo";
    }

    //Configuration for Docker!
    /*
    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.credential(MongoCredential.createCredential("root", "admin", "example".toCharArray()))
                .applyToClusterSettings(settings -> {
                    settings.hosts(singletonList(
                            new ServerAddress("127.0.0.1", 27017)
                    ));
                });
    }
     */

}
