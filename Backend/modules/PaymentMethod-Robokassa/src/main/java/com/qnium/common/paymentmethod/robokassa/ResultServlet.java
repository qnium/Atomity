package com.qnium.common.paymentmethod.robokassa;

import com.qnium.common.backend.core.Logger;
import com.qnium.common.paymentmethod.robokassa.data.PaymentSuccessRequest;
import com.qnium.common.paymentmethod.robokassa.exceptions.RobokassaException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles result requests from Robokassa.
 * Its URL should be specified as ResultURL in Robokassa store settings.
 * Both GET and POST methods are supported.
 */
public class ResultServlet extends HttpServlet {
    
    private final RobokassaPaymentMethodProvider provider;
    
    public ResultServlet(RobokassaPaymentMethodProvider provider) {
        this.provider = provider;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException {
        handleSuccessRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException {
        handleSuccessRequest(request, response);
    }
    
    private void handleSuccessRequest(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter wr;
        try {
            wr = response.getWriter();
        }
        catch (IOException ex) {
            Logger.log.error("Robokassa success servlet - writer error, skipping request", ex);
            return;
        }
        PaymentSuccessRequest req = extractParameters(request);
        try {
            provider.processPaymentSuccess(req);
            wr.append("OK" + req.getInvoiceId());
        }
        catch (RobokassaException ex) {
            Logger.log.error("Payment process error, invoice ID: " + req.getInvoiceId() + ", message: ", ex.getMessage());
            wr.append("FAIL" + req.getInvoiceId() + ex.getMessage());
        }
    }

    private PaymentSuccessRequest extractParameters(HttpServletRequest httpReq) {
        PaymentSuccessRequest req = new PaymentSuccessRequest();
        
        String invoiceIdStr = httpReq.getParameter("InvId");
        if (invoiceIdStr == null || invoiceIdStr.isEmpty()) {
            Logger.log.error(
                "Robokassa result request doesn't contain invoice ID");
        }
        
        try {
            int invoiceId = Integer.parseInt(invoiceIdStr);
            req.setInvoiceId(invoiceId);
        }
        catch (NumberFormatException ex) {
            Logger.log.error(
                "Robokassa result request contains incorrect invoice ID: " + invoiceIdStr);
        }
        
        String amountStr = httpReq.getParameter("OutSum");
        if (amountStr == null || amountStr.isEmpty()) {
            Logger.log.error(
                "Robokassa result request doesn't contain amount");
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            req.setAmount(amount);
        }
        catch (NumberFormatException ex) {
            Logger.log.error(
                "Robokassa result request contains incorrect amount: " + invoiceIdStr);
        }
        
        String signatureValue = httpReq.getParameter("SignatureValue");
        if (signatureValue == null || signatureValue.isEmpty()) {
            Logger.log.error(
                "Robokassa result request doesn't contain signature");
        }
        
        req.setSignatureValue(signatureValue);
        return req;
    }    
}
