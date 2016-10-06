/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.reports.dataobjects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ivan
 */
public class ReportData<T> {
    private Map<String, Object> parameters;
    private Collection<T> datasource;

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Collection<T> getDatasource() {
        return datasource;
    }

    public void setDatasource(Collection<T> datasource) {
        this.datasource = datasource;
    }
    
    public ReportData() {
        this.parameters = new HashMap<>();
    }
    
    public void addParameter(String name, Object value) {
        parameters.put(name, value);
    }
}
