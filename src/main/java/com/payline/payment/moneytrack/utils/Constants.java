package com.payline.payment.moneytrack.utils;

/**
 * Support for constants used everywhere in the plugin sources.
 */
public class Constants {

    /**
     * Keys for the entries in ContractConfiguration map.
     */
    public static class ContractConfigurationKeys {
        public static final String MERCHANT_API_TOKEN = "apiToken";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private ContractConfigurationKeys() {
        }
    }

    /**
     * Keys for the entries in PartnerConfiguration maps.
     */
    public static class PartnerConfigurationKeys {


        public static final String URL = "URL";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private PartnerConfigurationKeys() {
        }
    }

    /**
     * Keys for the entries in RequestContext data.
     */
    public static class RequestContextKeys {
        public static final String PARTNER_TRANSACTION_ID = "partnerTransactionId";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private RequestContextKeys() {
        }
    }

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private Constants() {
    }

}
