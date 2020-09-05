package com.mybank.transaction.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybank.transaction.entity.TransactionDetailsEntity;
import com.mybank.transaction.model.TransactionDetails;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransactionEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionEventListener.class);

    @Value("${transaction.creation.user:TransactionMs}")
    protected String creationUser;

    @Autowired
    PersistTransaction persistTransaction;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = "#{'${mybank.kafka.transaction.topics}'.split(',')}")
    void listener(ConsumerRecord<Long, Map<String, Object>> event) {
        LOGGER.info("Received event with key: {}", event.key());
        try {
            Map<String, Object> transactionDetailsMap = event.value();
            if (null != transactionDetailsMap ){
                TransactionDetails transactionDetails = objectMapper.convertValue(transactionDetailsMap, TransactionDetails.class);
                LOGGER.info("Start processing for accountId: {}", transactionDetails.getAccountId());
                TransactionDetailsEntity transactionDetailsEntity = convertTransactionDetailsToEntity(transactionDetails);
                persistTransaction.saveTransaction(transactionDetails, transactionDetailsEntity);
                LOGGER.info("Completed processing for accountId: {}", transactionDetails.getAccountId());
            } else{
                LOGGER.error("Invalid event. Processing will not be done. transactionDetails: {}", transactionDetailsMap);
            }
        } catch(Exception ex){
            LOGGER.error("Failed to process transaction. AccountId: {}", event.key(), ex);
        }

    }

    private TransactionDetailsEntity convertTransactionDetailsToEntity
            (TransactionDetails transactionDetails){
        LOGGER.info("Converting transactionDetails to entity for accountId: {}",
                transactionDetails.getAccountId());
        TransactionDetailsEntity transactionDetailsEntity = new TransactionDetailsEntity();
        transactionDetailsEntity.setAccountId(transactionDetails.getAccountId());
        transactionDetailsEntity.setAmount(transactionDetails.getAmount());
        transactionDetailsEntity.setDescription(transactionDetails.getDescription());
        transactionDetailsEntity.setStatus(transactionDetails.getStatus());
        transactionDetailsEntity.setTransactionId(transactionDetails.getTransactionId());
        transactionDetailsEntity.setTransactionType(transactionDetails.getTransactionType());
        transactionDetailsEntity.setInsertedBy(creationUser);
        transactionDetailsEntity.setUpdatedBy(creationUser);
        LOGGER.info("Converting transactionDetails to entity completed for accountId: {}",
                transactionDetails.getAccountId());

        return transactionDetailsEntity;
    }


}
