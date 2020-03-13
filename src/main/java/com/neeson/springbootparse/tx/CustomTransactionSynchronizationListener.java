package com.neeson.springbootparse.tx;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Create on 2020-03-13
 * Copyright (c) 2020 by XueBang Information Technology Co.Ltd.
 * All Rights Reserved, Designed By XueBangSoft
 * Copyright:  Copyright(C) 2014-2020
 * Company:    XueBang Information Technology Co.Ltd.
 *
 * @author Administrator
 */
@Component
public class CustomTransactionSynchronizationListener {

    @EventListener
    public void handle(ApplicationPreparedEvent event) {
        TransactionSynchronizationManager.registerSynchronization(new CustomTransactionSynchronization());
    }

}
