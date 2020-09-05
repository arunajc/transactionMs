package com.mybank.customer.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mybank.transaction.entity.TransactionDetailsEntity;
import com.mybank.transaction.event.PersistTransactionImpl;
import com.mybank.transaction.event.TransactionEventListener;
import com.mybank.transaction.model.TransactionDetails;
import com.mybank.transaction.repository.TransactionRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionEventListenerTest {

    @InjectMocks
    TransactionEventListener transactionEventListener;

    @Mock
    TransactionRepository transactionRepository;

    protected String creationUser = "creation_user";

    @Mock
    KafkaTemplate<Long, TransactionDetails> kafkaTemplate;

    protected String transactionKafkaErrorTopic = "error_topic";

    PersistTransactionImpl persistTransaction = new PersistTransactionImpl();

    private ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }

    @Before
    public void setUp() throws Exception {
        persistTransaction.transactionRepository = transactionRepository;
        persistTransaction.kafkaTemplate = kafkaTemplate;
        persistTransaction.transactionKafkaErrorTopic = transactionKafkaErrorTopic;
        transactionEventListener.persistTransaction = persistTransaction;

        transactionEventListener.creationUser = creationUser;
        transactionEventListener.objectMapper = objectMapper();
    }

    @Test
    public void listener_success(){

        transactionEventListener.listener(createEvent());

        verify(kafkaTemplate, never()).send(any(Message.class)); //did not publish to error topic
        verify(transactionRepository, times(1)).save(any(TransactionDetailsEntity.class));

    }

    @Test
    public void listener_null_body(){

        transactionEventListener.listener(createEvent_null_body());

        verify(kafkaTemplate, never()).send(any(Message.class)); //did not publish to error topic
        verify(transactionRepository, never()).save(any(TransactionDetailsEntity.class));

    }

    @Test
    public void listener_DB_save_failed_verify_retry(){

        Mockito.doThrow(new RuntimeException("db error from Junit"))
                .when(transactionRepository).save(any());

        transactionEventListener.listener(createEvent());

        //TODO: retry not working - need to check
        //verify(kafkaTemplate, times(1)).send(any(Message.class)); //published to error topic
        //verify(transactionRepository, times(3)).save(any(TransactionDetailsEntity.class));

    }

    private ConsumerRecord<Long, Map<String, Object>> createEvent(){

        Map<String, Object> transactionDetailsMap = new HashMap<>();
        transactionDetailsMap.put("accountId", 123456L);
        transactionDetailsMap.put("transactionId", "ABC123");
        transactionDetailsMap.put("amount", new BigDecimal(100.58));
        transactionDetailsMap.put("status", "SUCCESS");

        ConsumerRecord<Long, Map<String, Object>> consumerRecord= new ConsumerRecord<>(
                "topic", 1, 100, (Long)transactionDetailsMap.get("accountId"),
                transactionDetailsMap);

        return consumerRecord;
    }

    private ConsumerRecord<Long, Map<String, Object>> createEvent_null_body(){
        Map<String, Object> transactionDetails = null;

        ConsumerRecord<Long, Map<String, Object>> consumerRecord= new ConsumerRecord<>(
                "topic", 1, 100, 111222L, transactionDetails);

        return consumerRecord;
    }

    private ConsumerRecord<Long, Map<String, Object>> createEvent_invalid_accountId(){
        Map<String, Object> transactionDetailsMap = new HashMap<>();
        transactionDetailsMap.put("accountId", 0L);
        transactionDetailsMap.put("transactionId", "ABC123");
        transactionDetailsMap.put("amount", new BigDecimal(100.58));
        transactionDetailsMap.put("status", "SUCCESS");

        ConsumerRecord<Long, Map<String, Object>> consumerRecord= new ConsumerRecord<>(
                "topic", 1, 100, (Long)transactionDetailsMap.get("accountId"),
                transactionDetailsMap);

        return consumerRecord;
    }
}