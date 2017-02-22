/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.reports.interfaces;

import com.qnium.common.reports.dataobjects.ReportData;

/**
 *
 * @author Ivan
 */
public interface ReportHandler<InputType> {
    ReportData process(InputType request) throws Exception;
}
