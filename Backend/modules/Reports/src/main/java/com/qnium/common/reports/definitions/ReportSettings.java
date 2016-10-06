/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.reports.definitions;

import com.qnium.common.reports.dataobjects.ReportData;

/**
 *
 * @author Ivan
 */
public class ReportSettings {
    private String name;
    private ReportData reportData;
    private ReportFormat format;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReportData getReportData() {
        return reportData;
    }

    public void setReportData(ReportData reportData) {
        this.reportData = reportData;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public void setFormat(ReportFormat format) {
        this.format = format;
    } 
}
