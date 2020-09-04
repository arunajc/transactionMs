package com.mybank.transaction.event;

import com.mybank.transaction.entity.TransactionDetailsEntity;
import com.mybank.transaction.exception.TransactionSaveException;
import com.mybank.transaction.model.TransactionDetails;
import com.mybank.transaction.repository.TransactionRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionEventListener.class);

    @Autowired
    TransactionRepository transactionRepository;

    @Value("${transaction.creation.user:TransactionMs}")
    protected String creationUser;

    @Autowired
    KafkaTemplate<Long, TransactionDetails> kafkaTemplate;

    @Value("${mybank.kafka.transaction.error.topic}")
    protected String transactionKafkaErrorTopic;

    @KafkaListener(topics = "#{'${mybank.kafka.transaction.topics}'.split(',')}")
    void listener(ConsumerRecord<Long, TransactionDetails> event) {
        LOGGER.info("Received event with key: {}", event.key());
        try {
            TransactionDetails transactionDetails = event.value();
            if (null != transactionDetails && transactionDetails.getAccountId() > 0) {
                LOGGER.info("Start processing for accountId: {}", transactionDetails.getAccountId());
                TransactionDetailsEntity transactionDetailsEntity = convertTransactionDetailsToEntity(transactionDetails);
                saveTransaction(transactionDetails, transactionDetailsEntity);
                LOGGER.info("Completed processing for accountId: {}", transactionDetails.getAccountId());
            } else{
                LOGGER.error("Invalid event. Processing will not be done. transactionDetails: {}", transactionDetails);
            }
        } catch(Exception ex){
            LOGGER.error("Failed to process transaction. AccountId: {}", event.key(), ex);
        }

    }

    @Retryable(
            value = {TransactionSaveException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public void saveTransaction(
            TransactionDetails transactionDetails, TransactionDetailsEntity transactionDetailsEntity)
            throws TransactionSaveException {
        try {
            LOGGER.info("Start saving transaction in DB for accountId: {}", transactionDetailsEntity.getAccountId());
            transactionRepository.save(transactionDetailsEntity);
        } catch(Exception ex){
            LOGGER.warn("Error while saving transaction in DB for accountId: {} - Retry will be done",
                    transactionDetailsEntity.getAccountId(), ex);
            throw new TransactionSaveException(ex.getMessage());
        }
    }

    @Recover
    public void handleTransactionSaveFailure(
            TransactionSaveException tse, TransactionDetails transactionDetails,
            TransactionDetailsEntity transactionDetailsEntity){
        LOGGER.error("Error while saving transaction in DB for accountId: {} - Retry completed",
                transactionDetailsEntity.getAccountId());

        Message<TransactionDetails> kafkaMessage = MessageBuilder
                .withPayload(transactionDetails)
                .setHeader(KafkaHeaders.TOPIC, transactionKafkaErrorTopic)
                .setHeader(KafkaHeaders.MESSAGE_KEY, transactionDetails.getAccountId())
                .build();

        LOGGER.info("Start publishing error record to Kafka error topic for accountId: {}", transactionDetailsEntity.getAccountId());
        kafkaTemplate.send(kafkaMessage);
        LOGGER.info("Completed publishing error record to Kafka error topic for accountId: {}", transactionDetailsEntity.getAccountId());
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
