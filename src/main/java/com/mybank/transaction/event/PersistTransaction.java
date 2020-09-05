package com.mybank.transaction.event;

import com.mybank.transaction.entity.TransactionDetailsEntity;
import com.mybank.transaction.exception.TransactionSaveException;
import com.mybank.transaction.model.TransactionDetails;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

public interface PersistTransaction {

    @Retryable(
            value = {TransactionSaveException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    void saveTransaction(
            TransactionDetails transactionDetails, TransactionDetailsEntity transactionDetailsEntity)
            throws TransactionSaveException;

    @Recover
    void handleTransactionSaveFailure(
            TransactionSaveException tse, TransactionDetails transactionDetails,
            TransactionDetailsEntity transactionDetailsEntity);
}
