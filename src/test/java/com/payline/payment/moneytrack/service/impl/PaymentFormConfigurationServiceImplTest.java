package com.payline.payment.moneytrack.service.impl;

import com.payline.payment.moneytrack.MockUtils;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PaymentFormConfigurationServiceImplTest {
    private PaymentFormConfigurationServiceImpl service = new PaymentFormConfigurationServiceImpl();

    @Test
    void getPaymentFormConfiguration() {
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequest();
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration(request);

        Assertions.assertEquals(PaymentFormConfigurationResponseSpecific.class, response.getClass());
    }
}