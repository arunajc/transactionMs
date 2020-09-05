package com.mybank.transaction.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransactionDetails {

	private long accountId;
	private BigDecimal amount;
	private String transactionType;
	
	private String transactionId;
	private String status;
	private String description;

	private String insertedBy;
	private OffsetDateTime insertedDate;
	private String updatedBy;
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
