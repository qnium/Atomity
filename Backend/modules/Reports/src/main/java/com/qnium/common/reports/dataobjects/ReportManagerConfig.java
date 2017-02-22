/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.reports.dataobjects;

import com.qnium.common.reports.interfaces.ReportProvider;
import javax.servlet.ServletContext;

/**
 *
 * @author Ivan
 */
public class ReportManagerConfig {
    private String servletPath;
    private ServletContext servletContext;
    private ReportProvider reportProvider;

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ReportProvider getReportProvider() {
        return reportProvider;
    }

    public void setReportProvider(ReportProvider reportProvider) {
        this.reportProvider = reportProvider;
    }
}
