package com.mybank.transaction.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name="MYBANK_TRANSACTIONS")
public class TransactionDetailsEntity {

	@Id
	@Column(name="ACCOUNTID")
	private long accountId;

	@Column(name="AMOUNT")
	private BigDecimal amount;

	@Column(name="TRANSACTIONTYPE")
	private String transactionType;

	@Column(name="TRANSACTIONID")
	private String transactionId;

	@Column(name="STATUS")
	private String status;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="INSERTEDBY", updatable = false)
	private String insertedBy;

	@CreationTimestamp
	@Column(name="INSERTEDDATE", updatable = false)
	private OffsetDateTime insertedDate;

	@Column(name="UPDATEDBY")
	private String updatedBy;

	@UpdateTimestamp
	@Column(name="UPDATEDDATE")
	private OffsetDateTime updatedDate;
	/**
	 * @return the accountId
	 */
	public long getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}
	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	/**
	 * @return the insertedBy
	 */
	public String getInsertedBy() {
		return insertedBy;
	}
	/**
	 * @param insertedBy the insertedBy to set
	 */
	public void setInsertedBy(String insertedBy) {
		this.insertedBy = insertedBy;
	}
	/**
	 * @return the insertedDate
	 */
	public OffsetDateTime getInsertedDate() {
		return insertedDate;
	}
	/**
	 * @param insertedDate the insertedDate to set
	 */
	public void setInsertedDate(OffsetDateTime insertedDate) {
		this.insertedDate = insertedDate;
	}
	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}
	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	/**
	 * @return the updatedDate
	 */
	public OffsetDateTime getUpdatedDate() {
		return updatedDate;
	}
	/**
	 * @param updatedDate the updatedDate to set
	 */
	public void setUpdatedDate(OffsetDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}
	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
