/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.reports.core;

import com.qnium.common.reports.dataobjects.ReportManagerConfig;
import com.qnium.common.reports.definitions.ReportSettings;
import com.qnium.common.reports.interfaces.ReportHandler;
import com.qnium.common.reports.interfaces.ReportProvider;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletRegistration;
import org.codehaus.jackson.type.TypeReference;

/**
 *
 * @author Ivan
 */
public class ReportManager {
    
    private static ReportManager _instance;
    
    private final Map<String, String> reportNames = new HashMap<>();
    private final Map<String, ReportHandlerWrapper> reportWrappers = new HashMap<>();
    private ReportProvider provider;
    
    private ReportManager() {
    }
    
    public static synchronized ReportManager getInstance() {
        if (_instance == null) {
            _instance = new ReportManager();
        }
        return _instance;
    }
    
    public ReportHandlerWrapper getReportWrapper(String id) throws Exception {
        ReportHandlerWrapper wrapper = reportWrappers.get(id);
        if (wrapper == null) {
            throw new Exception("No report handler found for " + id);
        }
        return wrapper;
    }
    
    public void init(ReportManagerConfig config) {
        this.provider = config.getReportProvider();
        ReportServlet rs = new ReportServlet();
        ServletRegistration.Dynamic dyn = config
                .getServletContext()
                .addServlet("ReportServlet", rs);
        dyn.addMapping(config.getServletPath());
    }
    
    public void generateReport(ReportSettings settings, OutputStream reportStream)
            throws Exception {
        
        /*String reportName = reportNames.get(settings.getName());
        if (reportName == null) {
            throw new Exception("Report not found for " + settings.getName());
        }
        
        settings.setName(reportName);*/
        provider.generateReportFile(settings, reportStream);
    }
    
    public void registerReport(String handlerId, String name) {
        reportNames.put(handlerId, name);
    }
    
    public void addHandler(String handlerName, TypeReference requestType, ReportHandler handler)
            throws Exception {
        if(!reportWrappers.containsKey(handlerName)) {
            ReportHandlerWrapper wr = new ReportHandlerWrapper(requestType, handler);
            reportWrappers.put(handlerName, wr);
        }
        else {
            throw new Exception(String.format(
                "Report handler \"%s\" already added", handlerName));
        }
    }
}
