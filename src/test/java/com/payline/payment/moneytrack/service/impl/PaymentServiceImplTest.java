package com.payline.payment.moneytrack.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.moneytrack.MockUtils;
import com.payline.payment.moneytrack.bean.Transaction;
import com.payline.payment.moneytrack.exception.InvalidDataException;
import com.payline.payment.moneytrack.utils.http.HttpClient;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl service = new PaymentServiceImpl();

    @Mock
    private HttpClient client = HttpClient.getInstance();

    private Gson parser = new GsonBuilder().create();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentRequest() {

        Transaction transaction = parser.fromJson(MockUtils.transaction(Transaction.Status.CREATED), Transaction.class);
        Mockito.doReturn(transaction).when(client).createTransaction(any(), any());

        PaymentResponse response = service.paymentRequest(MockUtils.aPaylinePaymentRequest());
        Assertions.assertEquals(PaymentResponseRedirect.class, response.getClass());
    }

    @Test
    void paymentRequestWrongStatus() {
        Transaction transaction = parser.fromJson(MockUtils.transaction(Transaction.Status.FAILED), Transaction.class);
        Mockito.doReturn(transaction).when(client).createTransaction(any(), any());

        PaymentResponse response = service.paymentRequest(MockUtils.aPaylinePaymentRequest());
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void paymentRequestInvalidToken() {
        Mockito.doThrow(new InvalidDataException("Invalid token")).when(client).createTransaction(any(), any());

        PaymentResponse response = service.paymentRequest(MockUtils.aPaylinePaymentRequest());
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void paymentRequestInvalidData() {
        Mockito.doThrow(new InvalidDataException("Invalid URL")).when(client).createTransaction(any(), any());

        PaymentResponse response = service.paymentRequest(MockUtils.aPaylinePaymentRequest());
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void paymentRequestNPE() {
        Mockito.doThrow(new NullPointerException()).when(client).createTransaction(any(), any());

        PaymentResponse response = service.paymentRequest(MockUtils.aPaylinePaymentRequest());
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }
}