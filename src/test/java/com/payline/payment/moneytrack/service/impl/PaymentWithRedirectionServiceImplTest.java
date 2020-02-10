package com.payline.payment.moneytrack.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.moneytrack.MockUtils;
import com.payline.payment.moneytrack.bean.Transaction;
import com.payline.payment.moneytrack.bean.configuration.RequestConfiguration;
import com.payline.payment.moneytrack.exception.InvalidDataException;
import com.payline.payment.moneytrack.utils.http.HttpClient;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;

class PaymentWithRedirectionServiceImplTest {
    private Gson parser = new GsonBuilder().create();

    @InjectMocks
    PaymentWithRedirectionServiceImpl service = new PaymentWithRedirectionServiceImpl();

    @Mock
    HttpClient client = HttpClient.getInstance();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void finalizeRedirectionPayment() {
        RedirectionPaymentRequest request = MockUtils.aRedirectionPaymentRequest();

        Transaction transaction = parser.fromJson(MockUtils.transaction(Transaction.Status.PAID), Transaction.class);
        Mockito.doReturn(transaction).when(client).getTransaction(any(), any());

        PaymentResponse response = service.finalizeRedirectionPayment(request);
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
    }


    @Test
    void handleSessionExpired() {
        TransactionStatusRequest request = MockUtils.aTransactionStatusRequest();

        Transaction transaction = parser.fromJson(MockUtils.transaction(Transaction.Status.PAID), Transaction.class);
        Mockito.doReturn(transaction).when(client).getTransaction(any(), any());

        PaymentResponse response = service.handleSessionExpired(request);
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
    }

    @Test
    void getTransactionStatusKO() {
        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );
        String partnerTransactionId = "123123123";

        Transaction transaction = parser.fromJson(MockUtils.transaction(Transaction.Status.FAILED), Transaction.class);
        Mockito.doReturn(transaction).when(client).getTransaction(any(), any());

        PaymentResponse response = service.getTransactionStatus(configuration, partnerTransactionId);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void getTransactionStatusInvalidToken() {
        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );
        String partnerTransactionId = "123123123";

        Mockito.doThrow(new InvalidDataException("Invalid token")).when(client).getTransaction(any(), any());

        PaymentResponse response = service.getTransactionStatus(configuration, partnerTransactionId);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void getTransactionStatusInvalidData() {
        RedirectionPaymentRequest request = MockUtils.aRedirectionPaymentRequest();

        Mockito.doThrow(new InvalidDataException("Invalid URL")).when(client).getTransaction(any(), any());

        PaymentResponse response = service.finalizeRedirectionPayment(request);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void getTransactionStatusNPE() {
        RedirectionPaymentRequest request = MockUtils.aRedirectionPaymentRequest();

        Mockito.doThrow(new NullPointerException()).when(client).getTransaction(any(), any());

        PaymentResponse response = service.finalizeRedirectionPayment(request);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }


}