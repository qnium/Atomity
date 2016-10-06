/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentmethod.robokassa;

import com.qnium.common.backend.core.Logger;
import com.qnium.common.paymentmethod.robokassa.data.PaymentSuccessRequest;
import com.qnium.common.paymentmethod.robokassa.exceptions.RobokassaException;
import com.qnium.common.paymentsystem.dao.OrderManager;
import com.qnium.common.paymentsystem.dataobjects.PaymentPage;
import com.qnium.common.paymentsystem.dataobjects.PaymentParameters;
import com.qnium.common.paymentsystem.interfaces.PaymentMethodProvider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import javax.xml.bind.DatatypeConverter;

/**
 * Implementation of Robokassa payment provider.
 * @author Ivan Kashtanov
 */
public class RobokassaPaymentMethodProvider implements PaymentMethodProvider {

    private final String PROVIDER_NAME = "Robokassa";
    
    private String merchantLogin;
    private String password1;
    private String password2;
    private String hashAlgorithm;
    private MessageDigest hashGenerator;
    private String testMode;
    
    public RobokassaPaymentMethodProvider(ServletContext servletContext) throws ServletException{
        ResultServlet rs = new ResultServlet(this);
        Dynamic dyn = servletContext.addServlet(PROVIDER_NAME, rs);
        dyn.addMapping("/robokassa/*");
    }
    
    @Override
    public String getName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public PaymentPage requestPayment(PaymentParameters params) {
        PaymentPage page = new PaymentPage();
        
        long orderId = params.getOrderId();
        double amount = params.getAmount();        
        
        //page.setUrl(String.format("https://auth.robokassa.ru/Merchant/PaymentForm/FormMS.if?MrchLogin=%s&OutSum=%s&InvId=%s&Desc=%s&SignatureValue=%s",
        String url = String.format("https://auth.robokassa.ru/Merchant/Index.aspx?MrchLogin=%s&OutSum=%s&InvId=%s&Desc=%s&SignatureValue=%s",
                merchantLogin,
                String.format(Locale.US, "%.2f", params.getAmount()),
                params.getOrderId(),
                params.getDescription(),
                calculateSignatureValueForUrl(orderId, amount));
        
        if(testMode.equals("true")) {
            url += "&IsTest=1";
        }
        page.setUrl(url);
        
        return page;
    }

    @Override
    public void init(Map<String, String> parameters) {
        this.merchantLogin = parameters.get("MERCHANT_LOGIN_PARAM");
        this.password1 = parameters.get("PASSWORD1_PARAM");
        this.password2 = parameters.get("PASSWORD2_PARAM");
        this.hashAlgorithm = parameters.get("HASH_ALGORITHM_PARAM");
        this.testMode = parameters.get("TEST_MODE_PARAM");
        try {
            this.hashGenerator = MessageDigest.getInstance(this.hashAlgorithm);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(
                "Hash algorithm " + this.hashAlgorithm + " is not supported by provider", ex);
        }
    }
    
    public void processPaymentSuccess(PaymentSuccessRequest req)
            throws RobokassaException {
        if (verifyPaymentSuccess(req)) {
            try {
                OrderManager.getInstance().confirmPaymentForOrder(req.getInvoiceId());
            }
            catch (Exception ex) {
                throw new RobokassaException(
                    "Error while confirming payment for order " + req.getInvoiceId(), ex);
            }
        }
        else {
            String msg = "Robokassa success request signature is incorrect: " 
                    + req.getSignatureValue();
            Logger.log.error(msg);
            throw new RobokassaException(msg);
        }
    }
    
    private String calculateSignatureValueForUrl(long invoiceId, double amount) {
        StringBuilder sb = new StringBuilder();
        sb.append(merchantLogin);
        sb.append(':');
        sb.append(String.format(Locale.US, "%.2f", amount));
        sb.append(':');
        sb.append(invoiceId);
        sb.append(':');
        sb.append(password1);
        String hashedValue = sb.toString();
        byte[] hash = hashGenerator.digest(hashedValue.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(hash);
    }    
    
    private String calculateSignatureValueForResult(long invoiceId, double amount) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, "%.2f", amount));
        sb.append(':');
        sb.append(invoiceId);
        sb.append(':');
        sb.append(password2);
        String hashedValue = sb.toString();
        byte[] hash = hashGenerator.digest(hashedValue.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(hash);
    }
    
    /**
     * Checks that the given success request signature is valid.
     * @param req Success request
     * @return True if request signature is valid, false otherwise.
     */
    private boolean verifyPaymentSuccess(PaymentSuccessRequest req) {
        String calculatedSig = calculateSignatureValueForResult(req.getInvoiceId(), req.getAmount());
        return calculatedSig.equals(req.getSignatureValue());
    }
    
}
