/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.reports.interfaces;

import com.qnium.common.reports.definitions.ReportSettings;
import java.io.OutputStream;

/**
 *
 * @author Ivan
 */
public interface ReportProvider {
    
    /**
     * Generates report file using specified settings and puts it into
     * the specified stream.
     * @param settings Settings for report generation.
     * @param reportStream Output stream which receives report file content.
     * @throws Exception In case internal provider error occurs.
     */
    void generateReportFile(ReportSettings settings, OutputStream reportStream) throws Exception;
}
