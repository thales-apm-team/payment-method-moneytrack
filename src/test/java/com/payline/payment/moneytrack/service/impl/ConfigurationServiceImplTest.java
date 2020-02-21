package com.payline.payment.moneytrack.service.impl;

import com.payline.payment.moneytrack.MockUtils;
import com.payline.payment.moneytrack.exception.PluginException;
import com.payline.payment.moneytrack.utils.Constants;
import com.payline.payment.moneytrack.utils.http.HttpClient;
import com.payline.payment.moneytrack.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import org.apache.bcel.classfile.Constant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

class ConfigurationServiceImplTest {
    @InjectMocks
    private ConfigurationServiceImpl service = new ConfigurationServiceImpl();

    @Mock
    private HttpClient client;

    @Mock
    private ReleaseProperties releaseProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getParameters() {
        List<AbstractParameter> parameters = service.getParameters(new Locale("FR"));
        Assertions.assertEquals(2, parameters.size());
    }

    @Test
    void check() {
        Mockito.doNothing().when(client).checkConnection(any(), any());
        Map errors = service.check(MockUtils.aContractParametersCheckRequest());
        Assertions.assertEquals(0, errors.size());
    }

    @Test
    void checkEmptyToken() {
        Mockito.doNothing().when(client).checkConnection(any(), any());

        Map<String,String> accountInfo = new HashMap<>();
        accountInfo.put(Constants.ContractConfigurationKeys.MERCHANT_API_STORE, "foo");

        ContractParametersCheckRequest request = MockUtils.aContractParametersCheckRequestBuilder()
                .withAccountInfo(accountInfo)
                .build();

        Map errors = service.check(request);
        Assertions.assertEquals(1, errors.size());
        Assertions.assertNotNull(errors.get(Constants.ContractConfigurationKeys.MERCHANT_API_TOKEN));
    }

    @Test
    void checkEmptyStore() {
        Mockito.doNothing().when(client).checkConnection(any(), any());

        Map<String,String> accountInfo = new HashMap<>();
        accountInfo.put(Constants.ContractConfigurationKeys.MERCHANT_API_TOKEN, "foo");

        ContractParametersCheckRequest request = MockUtils.aContractParametersCheckRequestBuilder()
                .withAccountInfo(accountInfo)
                .build();

        Map errors = service.check(request);
        Assertions.assertEquals(1, errors.size());
        Assertions.assertNotNull(errors.get(Constants.ContractConfigurationKeys.MERCHANT_API_STORE));
    }

    @Test
    void checkKO() {
        Exception e = new PluginException("foo");

        Mockito.doThrow(e).when(client).checkConnection(any(), any());
        Map errors = service.check(MockUtils.aContractParametersCheckRequest());
        Assertions.assertEquals(1, errors.size());
    }


    @Test
    public void getReleaseInformation() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String version = "M.m.p";

        // given: the release properties are OK
        doReturn(version).when(releaseProperties).get("release.version");
        Calendar cal = new GregorianCalendar();
        cal.set(2019, Calendar.AUGUST, 19);
        doReturn(formatter.format(cal.getTime())).when(releaseProperties).get("release.date");

        // when: calling the method getReleaseInformation
        ReleaseInformation releaseInformation = service.getReleaseInformation();

        // then: releaseInformation contains the right values
        assertEquals(version, releaseInformation.getVersion());
        assertEquals(2019, releaseInformation.getDate().getYear());
        assertEquals(Month.AUGUST, releaseInformation.getDate().getMonth());
        assertEquals(19, releaseInformation.getDate().getDayOfMonth());
    }

    @Test
    void getName() {
        String name = service.getName(Locale.FRANCE);
        Assertions.assertNotNull(name);
        Assertions.assertFalse(name.isEmpty());
    }
}