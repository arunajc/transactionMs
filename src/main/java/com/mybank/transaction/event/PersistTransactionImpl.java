package com.mybank.transaction.event;

import com.mybank.transaction.entity.TransactionDetailsEntity;
import com.mybank.transaction.exception.TransactionSaveException;
import com.mybank.transaction.model.TransactionDetails;
import com.mybank.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class PersistTransactionImpl implements PersistTransaction{
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistTransactionImpl.class);

    @Autowired
    TransactionRepository transactionRepository;

    @Value("${mybank.kafka.transaction.error.topic}")
    protected String transactionKafkaErrorTopic;

    @Autowired
    KafkaTemplate<Long, TransactionDetails> kafkaTemplate;

    @Override
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

    @Override
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
}
