package com.payline.payment.moneytrack.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class MoneyTrackPaymentRequest {
    @SerializedName("merchant_reference")
    private String merchantReference;

    @SerializedName("currency_amount")
    private String currencyAmount;

    @SerializedName("callback_url")
    private String callbackURL;

    @SerializedName("return_url")
    private String returnURL;

    @SerializedName("order_info")
    private OrderInfo orderInfo;

    @SerializedName("expire_at")
    private String expireAt;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    private MoneyTrackPaymentRequest(Builder builder) {
        this.merchantReference = builder.merchantReference;
        this.currencyAmount = builder.currencyAmount;
        this.callbackURL = builder.callbackURL;
        this.returnURL = builder.returnURL;
        this.orderInfo = builder.orderInfo;
        this.expireAt = builder.expireAt;
    }

    public static class Builder {
        private String merchantReference;
        private String currencyAmount;
        private String callbackURL;
        private String returnURL;
        private OrderInfo orderInfo;
        private String expireAt;


        public static Builder aMoneyTrackPaymentRequest() {
            return new Builder();
        }

        public Builder withMerchantReference(String merchantReference) {
            this.merchantReference = merchantReference;
            return this;
        }

        public Builder withCurrencyAmount(String currencyAmount) {
            this.currencyAmount = currencyAmount;
            return this;
        }

        public Builder withCallbackURL(String callbackURL) {
            this.callbackURL = callbackURL;
            return this;
        }

        public Builder withReturnURL(String returnURL) {
            this.returnURL = returnURL;
            return this;
        }

        public Builder withOrderInfo(String orderInfo) {
            this.orderInfo = new OrderInfo(orderInfo);
            return this;
        }

        public Builder withExpireAt(String expireAt) {
            this.expireAt = expireAt;
            return this;
        }

        public MoneyTrackPaymentRequest build() {
            return new MoneyTrackPaymentRequest(this);
        }
    }


}
