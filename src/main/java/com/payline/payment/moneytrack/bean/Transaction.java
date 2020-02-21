package com.payline.payment.moneytrack.bean;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.moneytrack.utils.Constants;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.BuyerPaymentId;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.Email;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private String id;

    @SerializedName("merchant_reference")
    private String merchantReference;

    @SerializedName("total_amount")
    private String totalAmount;

    @SerializedName("refund_amount")
    private String refundAmount;

    @SerializedName("payment_url")
    private URL paymentURL;

    @SerializedName("callback_url")
    private URL callbackURL;

    @SerializedName("return_url")
    private URL returnURL;

    private String status;

    @SerializedName("payer_data")
    private PayerData payerData;

    @SerializedName("order_info")
    private OrderInfo orderInfo;

    @SerializedName("withdrawal_id")
    private String withdrawalId;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("paid_at")
    private Date paidAt;


    public String getId() {
        return id;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public URL getPaymentURL() {
        return paymentURL;
    }

    public URL getCallbackURL() {
        return callbackURL;
    }

    public URL getReturnURL() {
        return returnURL;
    }

    public String getStatus() {
        return status;
    }

    public PayerData getPayerData() {
        return payerData;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public String getWithdrawalId() {
        return withdrawalId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getPaidAt() {
        return paidAt;
    }

    public PaymentResponseRedirect toPaymentResponseRedirect() {
        PaymentResponseRedirect.RedirectionRequest redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                .aRedirectionRequest()
                .withUrl(this.getPaymentURL())
                .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                .build();

        Map<String, String> requestData = new HashMap<>();
        requestData.put(Constants.RequestContextKeys.PARTNER_TRANSACTION_ID, this.getId());
        RequestContext context = RequestContext.RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestData)
                .build();

        return PaymentResponseRedirect.PaymentResponseRedirectBuilder
                .aPaymentResponseRedirect()
                .withPartnerTransactionId(this.getId())
                .withRedirectionRequest(redirectionRequest)
                .withRequestContext(context)
                .withStatusCode(this.getStatus())
                .build();
    }

    public PaymentResponseSuccess toPaymentResponseSuccess() {
        BuyerPaymentId paymentId = Email.EmailBuilder
                .anEmail()
                .withEmail(this.getPayerData().getEmail())
                .build();

        return PaymentResponseSuccess.PaymentResponseSuccessBuilder
                .aPaymentResponseSuccess()
                .withPartnerTransactionId(this.getId())
                .withStatusCode(this.getStatus())
                .withTransactionDetails(paymentId)
                .build();
    }

    public PaymentResponseFailure toPaymentResponseFailure() {
        FailureCause cause;
        if (Transaction.Status.CANCELLED.equalsIgnoreCase(this.getStatus())) {
            cause = FailureCause.CANCEL;
        } else if (Transaction.Status.EXPIRED.equalsIgnoreCase(this.getStatus())) {
            cause = FailureCause.SESSION_EXPIRED;
        } else {
            cause = FailureCause.INVALID_DATA;
        }

        return PaymentResponseFailure.PaymentResponseFailureBuilder
                .aPaymentResponseFailure()
                .withPartnerTransactionId(this.getId())
                .withFailureCause(cause)
                .withErrorCode(this.getStatus())
                .build();
    }

    public PaymentResponseOnHold toPaymentResponseOnHold(){
        return PaymentResponseOnHold.PaymentResponseOnHoldBuilder
                .aPaymentResponseOnHold()
                .withPartnerTransactionId(this.getId())
                .withStatusCode(this.getStatus())
                .withOnHoldCause(OnHoldCause.ASYNC_RETRY)
                .build();
    }

    public static class Status {
        public static String CREATED = "created";
        public static String AUTHORIZATION_REQUESTED = "authorization_requested";
        public static String AUTHORIZED = "authorized";
        public static String CANCELLED = "canceled";
        public static String COMPLEMENTARY_PAYMENT_CREATED = "complementary_payment_created";
        public static String FAILED = "failed";
        public static String PROCESSING_REPAYMENT = "processing_repayment";
        public static String EXPIRED = "expired";
        public static String PAID = "paid";
        public static String REPAID = "repaid";
    }
}
