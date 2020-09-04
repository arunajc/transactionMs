package com.mybank.transaction.repository;

import com.mybank.transaction.entity.TransactionDetailsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionDetailsEntity, Long> {

}
