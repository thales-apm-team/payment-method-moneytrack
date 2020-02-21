package com.payline.payment.moneytrack.utils;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.payline.payment.moneytrack.exception.InvalidDataException;
import com.payline.pmapi.bean.common.Amount;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.stream.Collectors;

public class PluginUtils {
    static JsonParser parser = new JsonParser();


    private PluginUtils() {
        // ras.
    }

    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

    /**
     * Convert an InputStream into a String
     *
     * @param stream the InputStream to convert
     * @return the converted String encoded in UTF-8
     */
    public static String inputStreamToString(InputStream stream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return br.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Return a string which was converted from cents to currency amount
     *
     * @param amount the amount in cents
     * @return Amount as String
     */
    public static String createStringAmount(BigInteger amount, Currency currency) {
        int nbDigits = currency.getDefaultFractionDigits();

        StringBuilder sb = new StringBuilder();
        sb.append(amount);

        for (int i = sb.length(); i < 3; i++) {
            sb.insert(0, "0");
        }

        sb.insert(sb.length() - nbDigits, ".");
        return sb.toString();
    }

    public static String createStringAmount(Amount amount) {
        return createStringAmount(amount.getAmountInSmallestUnit(), amount.getCurrency());
    }

    /**
     * Check if a String is null or empty
     *
     * @param value the String to check
     * @return
     */
    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }


    /**
     * Get first error of an errorMessage sent by MoneyTrack
     *
     * @param json the message containing all errors
     * @return a simple message formatted as [firstErrorField]: [firstErrorMessage]
     */
    public static String getErrorMessage(String json) {
        try {
            JsonObject obj = parser.parse(json).getAsJsonObject().get("errors").getAsJsonObject();
            String k = obj.keySet().iterator().next();
            String v = obj.get(k).getAsJsonArray().get(0).getAsString();

            return k + ": " + v;
        }catch (NullPointerException e){
            throw new InvalidDataException("unable to read the error message");
        }
    }

}