package com.payline.payment.moneytrack.service.impl;

import com.payline.payment.moneytrack.bean.MoneyTrackPaymentRequest;
import com.payline.payment.moneytrack.bean.Transaction;
import com.payline.payment.moneytrack.bean.configuration.RequestConfiguration;
import com.payline.payment.moneytrack.exception.PluginException;
import com.payline.payment.moneytrack.utils.Constants;
import com.payline.payment.moneytrack.utils.PluginUtils;
import com.payline.payment.moneytrack.utils.http.HttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;

public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private HttpClient client = HttpClient.getInstance();


    @Override
    public PaymentResponse paymentRequest(PaymentRequest request) {
        RequestConfiguration configuration = new RequestConfiguration(
                request.getContractConfiguration()
                , request.getEnvironment()
                , request.getPartnerConfiguration()
        );
        try {

            MoneyTrackPaymentRequest moneyTrackPaymentRequest = MoneyTrackPaymentRequest.Builder
                    .aMoneyTrackPaymentRequest()
                    .withMerchantReference(request.getOrder().getReference())
                    .withCurrencyAmount(PluginUtils.createStringAmount(request.getAmount()))
                    .withCallbackURL(request.getEnvironment().getNotificationURL())
                    .withReturnURL(request.getEnvironment().getRedirectionReturnURL())
                    .withOrderInfo(request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.MERCHANT_API_STORE).getValue())
                    .build();

            Transaction transaction = client.createTransaction(configuration, moneyTrackPaymentRequest);

            if (Transaction.Status.CREATED.equalsIgnoreCase(transaction.getStatus())) {
                return transaction.toPaymentResponseRedirect();
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
