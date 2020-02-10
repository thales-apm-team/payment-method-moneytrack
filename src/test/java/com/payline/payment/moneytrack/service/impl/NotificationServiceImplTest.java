package com.payline.payment.moneytrack.service.impl;

import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NotificationServiceImplTest {
    private NotificationServiceImpl service = new NotificationServiceImpl();

    @Test
    void parse() {
        Assertions.assertEquals(IgnoreNotificationResponse.class, service.parse(null).getClass());
    }
}