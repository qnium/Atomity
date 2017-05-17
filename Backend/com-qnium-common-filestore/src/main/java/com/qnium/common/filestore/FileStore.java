/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.filestore;

import com.qnium.common.filestore.interfaces.FileStoreProvider;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

/**
 *
 * @author Drozhin
 */
public class FileStore
{
    private static String filesDirectory;
    private static long maxSizeOfUploadingFile; // size in bytes, -1 - no limit
    private static FileStoreProvider provider;
    
    public static void initialize(FileStoreParameters params)
    {
        initialize(params, true);
    }
    
    public static void initialize(FileStoreParameters params, boolean createServlet)
    {
        filesDirectory = params.filesDirectory;
        maxSizeOfUploadingFile = params.maxSizeOfUploadingFile;
        provider = params.provider;
        
        if (createServlet)
        {
        FileServlet fs = new FileServlet();
        ServletRegistration.Dynamic dyn = params.servletContext.addServlet("FileStoreServlet", fs);
        
        MultipartConfigElement me = new MultipartConfigElement(filesDirectory, 100*1024*1024, 100*1024*1024 * 2, 10 * 1024 * 1024);
        dyn.setMultipartConfig(me);
        dyn.addMapping(params.servletMappingPath);
        }
    }
    
    public static String getFilesDirectory(){
        return filesDirectory;
    }
    
    public static long getMaxSizeOfUploadingFile(){
        return maxSizeOfUploadingFile;
    }
    
    public static FileStoreProvider getProvider(){
        return provider;
    }
}
