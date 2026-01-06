package org.asupg.parser.service;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.asupg.parser.model.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CosmosDbService {

    private static final Logger logger = LoggerFactory.getLogger(CosmosDbService.class);

    private final CosmosContainer container;
    private final ObjectMapper objectMapper;

    private static CosmosClient cosmosClient = null;
    private static final Object lock = new Object();

    public CosmosDbService() {
        String endpoint = System.getenv("COSMOS_ENDPOINT");
        String key = System.getenv("COSMOS_KEY");
        String databaseName = System.getenv("COSMOS_DATABASE_NAME");
        String containerName = System.getenv("COSMOS_CONTAINER_NAME");

        if (cosmosClient == null) {
            synchronized (lock) {
                cosmosClient = new CosmosClientBuilder()
                        .endpoint(endpoint)
                        .key(key)
                        .consistencyLevel(ConsistencyLevel.SESSION)
                        .contentResponseOnWriteEnabled(false)
                        .buildClient();

                logger.info("CosmosDB client initialized");
            }
        }

        this.container = cosmosClient.getDatabase(databaseName).getContainer(containerName);
        this.objectMapper = new ObjectMapper();
    }

    public boolean saveTransactionIfNotExists(TransactionDTO transaction) {
        try {
            ObjectNode document = buildDocument(transaction);

            CosmosItemRequestOptions options = new CosmosItemRequestOptions();

            container.createItem(document, new PartitionKey(transaction.getDate().toString()), options);

            logger.info("Saved transaction as: {}", document.toString());
            return true;
        } catch (CosmosException e) {
            if (e.getStatusCode() == 409) {
                logger.info("Transaction already exists in CosmosDB");
                return false;
            } else {
                logger.error("CosmosDb error", e);
                throw new RuntimeException("Failed to save transaction", e);
            }
        }
    }

    public void bulkSaveTransaction(List<TransactionDTO> transactions) {
        if (transactions.isEmpty()) {
            logger.info("Transaction list is empty");
            return;
        }

        logger.info("Starting bulk save of {} transactions",  transactions.size());

        List<CosmosItemOperation> operations = transactions.stream()
                .map(transaction -> {
                    ObjectNode document = buildDocument(transaction);
                    return CosmosBulkOperations.getCreateItemOperation(
                            document,
                            new PartitionKey(transaction.getDate().toString())
                    );
                }).toList();

        Iterable<CosmosBulkOperationResponse<Object>> responses = container.executeBulkOperations(operations);

        int savedCount = 0;
        int duplicateCount = 0;
        int errorCount = 0;

        for (CosmosBulkOperationResponse<Object> response : responses) {
            if (response.getResponse() != null && response.getResponse().isSuccessStatusCode()) {
                savedCount++;
            } else if (response.getResponse() != null && response.getResponse().getStatusCode() == 409) {
                duplicateCount++;
            } else {
                errorCount++;
                if (response.getException() != null) {
                    logger.error("Error while saving transaction: {}",  response.getException().getMessage(), response.getException());
                } else {
                    logger.error("Error while saving transaction, status: {}",
                            response.getResponse() != null ? response.getResponse().getStatusCode() : "unknown");
                }
            }
        }

        logger.info("Bulk operation complete. Saved: {}, Duplicates: {}, Errors: {}", savedCount, duplicateCount, errorCount);
    }

    private ObjectNode buildDocument(TransactionDTO transaction) {
        ObjectNode document = objectMapper.createObjectNode();

        document.put("id", transaction.getTransactionId());

        // Partition key
        document.put("date", transaction.getDate().toString());

        // Keep transactionId for clarity/querying
        document.put("transactionId", transaction.getTransactionId());

        // Other fields
        if (transaction.getCounterpartyName() != null) {
            document.put("counterpartyName", transaction.getCounterpartyName());
        }
        if (transaction.getCounterpartyInn() != null) {
            document.put("counterpartyInn", transaction.getCounterpartyInn());
        }
        if (transaction.getAccountNumber() != null) {
            document.put("accountNumber", transaction.getAccountNumber());
        }
        if (transaction.getMfo() != null) {
            document.put("mfo", transaction.getMfo());
        }
        if (transaction.getAmount() != null) {
            document.put("amount", transaction.getAmount());
        }
        if (transaction.getDescription() != null) {
            document.put("description", transaction.getDescription());
        }

        // Metadata
        document.put("createdAt", System.currentTimeMillis());

        return document;
    }

}
