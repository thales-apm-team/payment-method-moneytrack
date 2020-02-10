package com.payline.payment.moneytrack.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.moneytrack.MockUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BeanTest {
    private Gson parser = new GsonBuilder().create();


    @Test
    void createResponseTransaction() {
        Transaction transaction = parser.fromJson(MockUtils.transaction(Transaction.Status.PAID), Transaction.class);

        Assertions.assertNotNull(transaction.getId());
        Assertions.assertNotNull(transaction.getTotalAmount());
        Assertions.assertNotNull(transaction.getRefundAmount());
        Assertions.assertNotNull(transaction.getPaymentURL());
        Assertions.assertNotNull(transaction.getCallbackURL());
        Assertions.assertNotNull(transaction.getReturnURL());
        Assertions.assertNotNull(transaction.getStatus());
        Assertions.assertNotNull(transaction.getPayerData());
        Assertions.assertNotNull(transaction.getPayerData().getEmail());
        Assertions.assertNotNull(transaction.getPayerData().getName());
        Assertions.assertNotNull(transaction.getOrderInfo());
        Assertions.assertNotNull(transaction.getOrderInfo().getStore());
    }

    @Test
    void createMoneyTrackPaymentRequest() {
        String expected = "{" +
                "\"merchant_reference\": \"121212121\"," +
                "\"currency_amount\": \"12.34\"," +
                "\"callback_url\": \"https://merchantstore.example.org/payment_callback_url\"," +
                "\"return_url\": \"https://example.org/store/\"," +
                "\"order_info\": {" +
                "\"store\": \"foo\"" +
                "}," +
                "\"expire_at\": \"2222-06-06T10:44:12+02:00\"" +
                "}";

        MoneyTrackPaymentRequest request = MoneyTrackPaymentRequest.Builder
                .aMoneyTrackPaymentRequest()
                .withMerchantReference("121212121")
                .withCurrencyAmount("12.34")
                .withCallbackURL("https://merchantstore.example.org/payment_callback_url")
                .withReturnURL("https://example.org/store/")
                .withOrderInfo("foo")
                .withExpireAt("2222-06-06T10:44:12+02:00")
                .build();


        Assertions.assertEquals(expected.replace(" ", ""), request.toString().replace(" ", ""));
    }
}
