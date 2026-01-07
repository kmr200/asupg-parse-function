package org.asupg.parser.config;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class DaggerModule {

    @Provides
    @Singleton
    @Named("cosmosEndpoint")
    public String provideEndpoint() {
        return System.getenv("COSMOS_ENDPOINT");
    }

    @Provides
    @Singleton
    @Named("cosmosKey")
    public String provideKey() {
        return System.getenv("COSMOS_KEY");
    }

    @Provides
    @Singleton
    @Named("cosmosDatabaseName")
    public String provideDatabaseName() {
        return System.getenv("COSMOS_DATABASE_NAME");
    }

    @Provides
    @Singleton
    @Named("cosmosContainerName")
    public String provideDatabase() {
        return System.getenv("COSMOS_CONTAINER_NAME");
    }

    @Provides
    @Singleton
    public CosmosClient provideCosmosClient(
            @Named("cosmosEndpoint") String cosmosEndpoint,
            @Named("cosmosKey") String cosmosKey
    ) {
        return new CosmosClientBuilder()
                .endpoint(cosmosEndpoint)
                .key(cosmosKey)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .contentResponseOnWriteEnabled(false)
                .buildClient();
    }

    @Provides
    @Singleton
    public CosmosContainer provideCosmosContainer(
            CosmosClient cosmosClient,
            @Named("cosmosDatabaseName") String cosmosDatabaseName,
            @Named("cosmosContainerName")  String cosmosContainerName
    ) {
        return cosmosClient.getDatabase(cosmosDatabaseName).getContainer(cosmosContainerName);
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

}
