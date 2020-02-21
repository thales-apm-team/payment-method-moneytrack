package com.payline.payment.moneytrack.service.impl;

import com.payline.payment.moneytrack.bean.Transaction;
import com.payline.payment.moneytrack.bean.configuration.RequestConfiguration;
import com.payline.payment.moneytrack.exception.PluginException;
import com.payline.payment.moneytrack.utils.Constants;
import com.payline.payment.moneytrack.utils.http.HttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.apache.logging.log4j.Logger;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentWithRedirectionServiceImpl.class);

    private HttpClient client = HttpClient.getInstance();

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest request) {
        RequestConfiguration configuration = new RequestConfiguration(
                request.getContractConfiguration()
                , request.getEnvironment()
                , request.getPartnerConfiguration()
        );
        String partnerTransactionId = request.getRequestContext().getRequestData().get(Constants.RequestContextKeys.PARTNER_TRANSACTION_ID);

        return getTransactionStatus(configuration, partnerTransactionId);
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest request) {
        RequestConfiguration configuration = new RequestConfiguration(
                request.getContractConfiguration()
                , request.getEnvironment()
                , request.getPartnerConfiguration()
        );
        String partnerTransactionId = request.getTransactionId();

        return getTransactionStatus(configuration, partnerTransactionId);
    }


    PaymentResponse getTransactionStatus(RequestConfiguration configuration, String partnerTransactionId) {
        try {
            Transaction transaction = client.getTransaction(configuration, partnerTransactionId);

            if (Transaction.Status.PROCESSING_REPAYMENT.equalsIgnoreCase(transaction.getStatus())) {
                return transaction.toPaymentResponseOnHold();
            } else if(Transaction.Status.PAID.equalsIgnoreCase(transaction.getStatus())){
                return transaction.toPaymentResponseSuccess();
            } else {
                return transaction.toPaymentResponseFailure();
            }
        } catch (PluginException e) {
            return e.toPaymentResponseFailureBuilder().build();
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
    }
}
