package com.mybank.transaction.exception;

public class TransactionSaveException extends Exception{

    private static final long serialVersionUID = 5106186594287000790L;
    private String message;

    public TransactionSaveException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
