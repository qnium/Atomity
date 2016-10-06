/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.reports.core;

import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.ResponseMessage;
import com.qnium.common.backend.core.AuthManager;
import com.qnium.common.backend.core.Logger;
import com.qnium.common.reports.dataobjects.ReportData;
import com.qnium.common.reports.definitions.ReportFormat;
import com.qnium.common.reports.definitions.ReportSettings;
import java.io.IOException;
import java.util.Base64;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ivan
 */
public class ReportServlet extends HttpServlet
{   
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            String jsonBase64 = request.getParameter("request");
            String json = new String(Base64.getDecoder().decode(jsonBase64));
            RequestMessage req = mapper.readValue(json, RequestMessage.class);
            
            AuthManager.getInstance().checkPermission(req.sessionKey, req.entityName, req.action);
            
            String handlerId = req.entityName + "." + req.action;
            ReportManager rm = ReportManager.getInstance();
            ReportHandlerWrapper wr = rm.getReportWrapper(handlerId);
            RequestMessage requestMessage = mapper.readValue(json, wr.getRequestType());
            ReportData reportData = wr.getHandler().process(requestMessage);
            
            ReportSettings settings = new ReportSettings();
            settings.setName(handlerId);
            settings.setReportData(reportData);
            
            ReportFormat format = ReportFormat.PDF;
            try {
                format = ReportFormat.valueOf(req.reportFormat);
            }
            catch (Exception ex) {
                Logger.log.error("Incorrect report format [" + req.reportFormat +
                        "] set for " + handlerId + ", using PDF");
            }
            
            settings.setFormat(format);
            
            if (req.downloadReport) {
                response.setContentType("application/x-download");
                response.setHeader(
                    "Content-Disposition", 
                    "attachment; filename=report." + format.toString().toLowerCase());
            }
            rm.generateReport(settings, response.getOutputStream());
            
        }
        catch (Exception ex)
        {
            Logger.log.error("Failed to process report servlet request", ex);
            try {
                response.setContentType("application/json");
                response.setHeader("Content-Disposition", null);
                ResponseMessage errorResponse = new ResponseMessage();
                errorResponse.error = ex.getMessage();
                
                mapper.writeValue(response.getOutputStream(), errorResponse);
            }
            catch (IOException ex1) {
                Logger.log.error("Failed to send report error response", ex1);
            }
        }
    }
}
