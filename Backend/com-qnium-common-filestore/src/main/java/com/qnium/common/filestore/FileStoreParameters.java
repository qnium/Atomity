/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.filestore;

import com.qnium.common.filestore.interfaces.FileStoreProvider;
import javax.servlet.ServletContext;

/**
 *
 * @author Drozhin
 */
public class FileStoreParameters
{
    public String filesDirectory;
    public long maxSizeOfUploadingFile;
    public FileStoreProvider provider;    
    public ServletContext servletContext;
    public String servletMappingPath;
}
