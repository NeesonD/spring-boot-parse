package com.neeson.springbootparse.tx;


import org.springframework.transaction.support.TransactionSynchronization;

public class CustomTransactionSynchronization implements TransactionSynchronization {

    @Override
    public void beforeCommit(boolean readOnly) {
    }

    @Override
    public void beforeCompletion() {
    }

    @Override
    public void afterCommit() {
    }

    @Override
    public void afterCompletion(int status) {
    }

}
