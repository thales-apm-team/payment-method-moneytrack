package com.payline.payment.moneytrack.utils.http;

import com.payline.payment.moneytrack.MockUtils;
import com.payline.payment.moneytrack.bean.MoneyTrackPaymentRequest;
import com.payline.payment.moneytrack.bean.Transaction;
import com.payline.payment.moneytrack.bean.configuration.RequestConfiguration;
import com.payline.payment.moneytrack.exception.PluginException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;

class HttpClientTest {

    @Spy
    @InjectMocks
    private HttpClient client = HttpClient.getInstance();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void checkConnection() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.transaction(Transaction.Status.CREATED)
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());


        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Assertions.assertDoesNotThrow(() -> client.checkConnection(configuration, "aToken"));
    }

    @Test
    void checkConnectionInvalidToken() {
        StringResponse stringResponse = MockUtils.mockStringResponse(401
                , "OK"
                , "{\"error\": \"Signature verification raised\"}"
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Assertions.assertThrows(PluginException.class, () -> client.checkConnection(configuration, "aToken"));
    }

    @Test
    void createTransaction() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.transaction(Transaction.Status.CREATED)
                , null);
        Mockito.doReturn(stringResponse).when(client).post(any(), any(), any());


        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        MoneyTrackPaymentRequest request = MoneyTrackPaymentRequest.Builder.aMoneyTrackPaymentRequest().build();
        Transaction transaction = client.createTransaction(configuration, request);

        Assertions.assertNotNull(transaction);
        Assertions.assertEquals(Transaction.Status.CREATED, transaction.getStatus());
    }


    @Test
    void getTransaction() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.transaction(Transaction.Status.PAID)
                , null);

        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );
        String partnerTransactionId = "123123";
        Transaction transaction = client.getTransaction(configuration, partnerTransactionId);

        Assertions.assertEquals(Transaction.Status.PAID, transaction.getStatus());
    }


    @Test
    void getTransactionInvalidToken() {
        StringResponse stringResponse = MockUtils.mockStringResponse(401
                , "foo"
                , "{\"error\": \"Signature verification raised\"}"
                , null);

        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );
        String partnerTransactionId = "123123";

        Assertions.assertThrows(PluginException.class, () -> client.getTransaction(configuration, partnerTransactionId));
    }


    @Test
    void getTransactionInvalid() {
        StringResponse stringResponse = MockUtils.mockStringResponse(500
                , "foo"
                , "{" +
                        "    \"errors\": {" +
                        "        \"return_url\": [" +
                        "            \"n'est pas une URL valide\"," +
                        "            \"n'est pas une URL autorisée\"" +
                        "        ]," +
                        "        \"callback_url\": [" +
                        "            \"n'est pas une URL autorisée\"" +
                        "        ]" +
                        "    }" +
                        "}"
                , null);

        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );
        String partnerTransactionId = "123123";

        Assertions.assertThrows(PluginException.class, () -> client.getTransaction(configuration, partnerTransactionId));
    }
}