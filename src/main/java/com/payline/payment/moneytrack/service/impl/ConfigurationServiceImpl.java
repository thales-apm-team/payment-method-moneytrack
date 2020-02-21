package com.payline.payment.moneytrack.service.impl;

import com.payline.payment.moneytrack.bean.configuration.RequestConfiguration;
import com.payline.payment.moneytrack.exception.PluginException;
import com.payline.payment.moneytrack.utils.Constants;
import com.payline.payment.moneytrack.utils.PluginUtils;
import com.payline.payment.moneytrack.utils.http.HttpClient;
import com.payline.payment.moneytrack.utils.i18n.I18nService;
import com.payline.payment.moneytrack.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ConfigurationService;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationServiceImpl.class);

    private ReleaseProperties releaseProperties = ReleaseProperties.getInstance();
    private I18nService i18n = I18nService.getInstance();
    private HttpClient client = HttpClient.getInstance();

    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        // token inputParameter
        AbstractParameter token = new InputParameter();
        token.setKey(Constants.ContractConfigurationKeys.MERCHANT_API_TOKEN);
        token.setLabel(i18n.getMessage("token.label", locale));
        token.setDescription(i18n.getMessage("token.description", locale));
        token.setRequired(true);
        parameters.add(token);

        AbstractParameter store = new InputParameter();
        store.setKey(Constants.ContractConfigurationKeys.MERCHANT_API_STORE);
        store.setLabel(i18n.getMessage("store.label", locale));
        store.setDescription(i18n.getMessage("store.description", locale));
        store.setValue("Online");
        store.setRequired(true);
        parameters.add(store);

        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest request) {
        final Locale locale = request.getLocale();
        final Map<String, String> errors = new HashMap<>();
        final RequestConfiguration configuration = new RequestConfiguration(
                request.getContractConfiguration()
                , request.getEnvironment()
                , request.getPartnerConfiguration()
        );
        try {
            String token = request.getAccountInfo().get(Constants.ContractConfigurationKeys.MERCHANT_API_TOKEN);
            String store = request.getAccountInfo().get(Constants.ContractConfigurationKeys.MERCHANT_API_STORE);
            if (PluginUtils.isEmpty(token)) {
                errors.put(Constants.ContractConfigurationKeys.MERCHANT_API_TOKEN, i18n.getMessage("token.empty", locale));
            } else if(PluginUtils.isEmpty(store)){
                errors.put(Constants.ContractConfigurationKeys.MERCHANT_API_STORE, i18n.getMessage("store.empty", locale));
            } else {
                client.checkConnection(configuration, token);
            }
        } catch (PluginException e) {
            errors.put(Constants.ContractConfigurationKeys.MERCHANT_API_TOKEN, i18n.getMessage("token.invalid", locale));
        }
        return errors;
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate(LocalDate.parse(releaseProperties.get("release.date"), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .withVersion(releaseProperties.get("release.version"))
                .build();
    }

    @Override
    public String getName(Locale locale) {
        return i18n.getMessage("paymentMethod.name", locale);
    }
}
