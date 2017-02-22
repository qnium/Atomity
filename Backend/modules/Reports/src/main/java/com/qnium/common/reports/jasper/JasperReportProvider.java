/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.reports.jasper;

import com.qnium.common.reports.dataobjects.ReportData;
import com.qnium.common.reports.definitions.ReportSettings;
import com.qnium.common.reports.interfaces.ReportProvider;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

/**
 *
 * @author Ivan
 */
public class JasperReportProvider implements ReportProvider {

    private String templatePath;
    
    /**
     * Creates generator with default template path which is "reports" directory
     * in current working directory.
     */
    public JasperReportProvider() {
        this(System.getProperty("user.dir") + "/reports/");
    }
    
    /**
     * Creates generator with specified template path.
     * @param templatePath Path to dir with templates. Report names are
     * relative path to template files without extension
     * (e.g. "MyReport" for %template_dir%/MyReport.jrxml template).
     */
    public JasperReportProvider(String templatePath) {
        this.templatePath = templatePath;
    }
    
    
    @Override
    public void generateReportFile(ReportSettings settings, OutputStream reportStream)
            throws Exception {
        String fileName = templatePath + settings.getName() + ".jrxml";
        JasperReport report = JasperCompileManager.compileReport(JRXmlLoader.load(fileName));
        
        ReportData reportData = settings.getReportData();

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData.getDatasource());
        
        reportData.addParameter("REPORT_DIRECTORY", templatePath);

        JasperPrint jPrint = JasperFillManager.fillReport(report, reportData.getParameters(), dataSource);
        List<JasperPrint> printList = new ArrayList<>();
        printList.add(jPrint);
        
        switch (settings.getFormat()) {
            case PDF:
                generatePdf(printList, reportStream);
                break;
            case CSV:
                generateCsv(printList, reportStream);
                break;
            case DOC:
                generateDoc(printList, reportStream);
                break;
            case XLS:
                generateXls(printList, reportStream);
                break;     
        }
    }
	
    private void generatePdf(List<JasperPrint> printList, OutputStream reportStream)
            throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        SimpleOutputStreamExporterOutput soutExp = 
                new SimpleOutputStreamExporterOutput(reportStream);
        exporter.setExporterOutput(soutExp);
        exporter.setExporterInput(SimpleExporterInput.getInstance(printList));
        exporter.exportReport();
    }
	
    private void generateXls(List<JasperPrint> printList, OutputStream reportStream)
            throws JRException {
        JRXlsExporter exporter = new JRXlsExporter();
        SimpleOutputStreamExporterOutput soutExp = 
            new SimpleOutputStreamExporterOutput(reportStream);
        exporter.setExporterOutput(soutExp);
        exporter.setExporterInput(SimpleExporterInput.getInstance(printList));
        
        SimpleXlsReportConfiguration config = new SimpleXlsReportConfiguration();
        config.setDetectCellType(true);
        config.setWhitePageBackground(true);
        config.setRemoveEmptySpaceBetweenRows(true);
        config.setRemoveEmptySpaceBetweenColumns(true);
        
        exporter.exportReport();
    }
    
    private void generateDoc(List<JasperPrint> printList, OutputStream reportStream) 
            throws JRException {
        JRDocxExporter exporter = new JRDocxExporter();
        SimpleOutputStreamExporterOutput soutExp = 
                new SimpleOutputStreamExporterOutput(reportStream);
        exporter.setExporterOutput(soutExp);
        exporter.setExporterInput(SimpleExporterInput.getInstance(printList));
        exporter.exportReport();
    }
    
    private void generateCsv(List<JasperPrint> printList, OutputStream reportStream) 
            throws JRException {
        JRCsvExporter exporter = new JRCsvExporter();
        SimpleWriterExporterOutput soutExp = 
                new SimpleWriterExporterOutput(reportStream);
        exporter.setExporterOutput(soutExp);
        exporter.setExporterInput(SimpleExporterInput.getInstance(printList));
        exporter.exportReport();
    }
    
}
