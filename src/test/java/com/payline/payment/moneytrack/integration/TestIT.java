package com.payline.payment.moneytrack.integration;


import com.payline.payment.moneytrack.MockUtils;
import com.payline.payment.moneytrack.service.impl.ConfigurationServiceImpl;
import com.payline.payment.moneytrack.service.impl.PaymentServiceImpl;
import com.payline.payment.moneytrack.service.impl.PaymentWithRedirectionServiceImpl;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.integration.AbstractPaymentIntegration;
import com.payline.pmapi.service.ConfigurationService;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestIT extends AbstractPaymentIntegration {
    private ConfigurationService configurationService = new ConfigurationServiceImpl();
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();
    private PaymentWithRedirectionServiceImpl paymentWithRedirectionService = new PaymentWithRedirectionServiceImpl();

    @Override
    protected Map<String, ContractProperty> generateParameterContract() {
        Map<String, ContractProperty> propertyHashMap = new HashMap<>();

        return propertyHashMap;
    }

    @Override
    protected PaymentFormContext generatePaymentFormContext() {
        return null;
    }

    @Override
    protected String payOnPartnerWebsite(String partnerUrl) {
        // Start browser
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
        try {

            // Go to partner's website
            driver.get(partnerUrl);

            // todo faire passer ces valeur en parametre du lancement de l'appli (voir ce qui est fait sur equens)
            setValue(driver, "#beneficiary_email", "martin.grescu@mythalesgroup.io");
            setValue(driver, "#beneficiary_password", "Thales01*");

            clickOn(driver, "#new_beneficiary input[name=\"commit\"]");

            /*
            here we have to manually enter the 3DSecure code on the payment page
             */

            WebDriverWait wait = new WebDriverWait(driver, 30);


            // Wait for redirection to success or cancel url
            wait.until(ExpectedConditions.or(ExpectedConditions.urlToBe("https://example.org/store/redirection")));
            return driver.getCurrentUrl();
        } finally {
            driver.quit();
        }
    }

    private WebElement goTo(WebDriver driver, String cssSelector) {
        WebElement element = driver.findElement(By.cssSelector(cssSelector));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        return element;
    }

    private void clickOn(WebDriver driver, String cssSelector) {
        goTo(driver, cssSelector).click();
    }

    private void setValue(WebDriver driver, String cssSelector, String value) {
        goTo(driver, cssSelector).sendKeys(value);
    }


    @Override
    protected String cancelOnPartnerWebsite(String s) {
        return null;
    }


    @Test
    void fullPaymentTest() {
        // test the check connection
        Map<String, String> errors = configurationService.check(MockUtils.aContractParametersCheckRequest());
        Assertions.assertEquals(0, errors.size());

        PaymentRequest request = createDefaultPaymentRequest();

        PaymentResponse paymentResponseFromPaymentRequest = paymentService.paymentRequest(request);
        Assertions.assertEquals(PaymentResponseRedirect.class, paymentResponseFromPaymentRequest.getClass());
        PaymentResponseRedirect paymentResponseRedirect = (PaymentResponseRedirect)paymentResponseFromPaymentRequest;
        String partnerTransactionId = paymentResponseRedirect.getPartnerTransactionId();

        String partnerUrl = paymentResponseRedirect.getRedirectionRequest().getUrl().toString();
        String redirectionUrl = this.payOnPartnerWebsite(partnerUrl);
        Assertions.assertEquals("https://succesurl.com/", redirectionUrl);

        RedirectionPaymentRequest redirectionPaymentRequest = RedirectionPaymentRequest.builder()
                .withContractConfiguration(MockUtils.aContractConfiguration())
                .withPaymentFormContext(this.generatePaymentFormContext())
                .withEnvironment(MockUtils.anEnvironment())
                .withTransactionId(request.getTransactionId())
                .withRequestContext(paymentResponseRedirect.getRequestContext())
                .withAmount(request.getAmount())
                .build();

        PaymentResponse paymentResponseFromFinalize = paymentWithRedirectionService.finalizeRedirectionPayment(redirectionPaymentRequest);
        Assertions.assertEquals(PaymentResponseSuccess.class, paymentResponseFromFinalize.getClass());
        PaymentResponseSuccess paymentResponseSuccess = (PaymentResponseSuccess)paymentResponseFromFinalize;
        Assertions.assertNotNull(paymentResponseSuccess.getTransactionDetails());
        Assertions.assertEquals(partnerTransactionId, paymentResponseSuccess.getPartnerTransactionId());

    }

    @Override
    public PaymentRequest createDefaultPaymentRequest() {
        return MockUtils.aPaylinePaymentRequest();
    }

}
