/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.reports.core;

import com.qnium.common.reports.interfaces.ReportHandler;
import org.codehaus.jackson.type.TypeReference;

/**
 *
 * @author Ivan
 */
public class ReportHandlerWrapper {
    private TypeReference requestType;
    private ReportHandler handler;

    public TypeReference getRequestType() {
        return requestType;
    }

    public void setRequestType(TypeReference requestType) {
        this.requestType = requestType;
    }

    public ReportHandler getHandler() {
        return handler;
    }

    public void setHandler(ReportHandler handler) {
        this.handler = handler;
    }

    public ReportHandlerWrapper(TypeReference requestType, ReportHandler handler) {
        this.requestType = requestType;
        this.handler = handler;
    }
    
}
