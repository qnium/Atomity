/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.filestore;

import com.qnium.common.backend.core.Logger;
import com.qnium.common.backend.exceptions.CommonException;
import com.qnium.common.filestore.dao.FileManager;
import com.qnium.common.filestore.dataobjects.FileRecord;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 *
 * @author Drozhzhin Dmitry
 */
public class FileServlet extends HttpServlet
{    
    private final Random random = new Random();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        byte[] downloadingData;
        
        try
        {
            String sessionKey = request.getParameter("sessionKey");
            long fileRecordId = Long.parseLong(request.getParameter("id"));            
            
            FileManager fileManager = FileManager.getInstance();
            FileRecord file = fileManager.getFileRecord(fileRecordId);
            
            if(file.viewForAll || FileStore.getProvider().checkDownloadRights(sessionKey, fileRecordId))
            {                
                downloadingData = fileManager.readFile(fileRecordId);                
                //response.setContentType("text/plain");
                response.setContentType("application/octet-stream");
                
                /*
                 * Content-Disposition header value is set to be able to handle
                 * files with non-ASCII names.
                 */
                String encodedFileName = URLEncoder
                    .encode(file.originalFileName, StandardCharsets.UTF_8.name())
                    // Spaces should remain as such, not converted to plus signs
                    .replace("+", "%20");
                String originalFileName = "filename=" + encodedFileName + "; ";
                String utf8FileName = "filename*=UTF-8''" + encodedFileName;
                response.setHeader("Content-Disposition", "attachment;" + originalFileName + utf8FileName);
                
                response.setStatus(HttpServletResponse.SC_OK);
                response.getOutputStream().write(downloadingData);                                
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }            
        }
        catch(Exception ex){
            Logger.log.error("File dowloading error.", ex.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {   
            String sessionKey = null;
            
            List<FileItem> fileItems = extractFileItems(request);
            
            for(FileItem fileItem : fileItems)
            {
                if(fileItem.isFormField() && fileItem.getFieldName().equals("sessionKey")){
                    sessionKey = new String(fileItem.get());
                }                    
            }            
            
            if(FileStore.getProvider().checkUploadRights(sessionKey))
            {            
                FileManager fileManager = FileManager.getInstance();
                for(FileItem fileItem : fileItems)
                {
                    if(!fileItem.isFormField())
                    {
                        java.io.File uploadedFile = uploadFile(fileItem);
                        FileRecord createdFileRecord = fileManager.createUploadedFile(uploadedFile, fileItem.getName());
                        response.addHeader("fileId", String.valueOf(createdFileRecord.id));
                        response.getWriter().print(createdFileRecord.id);
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.flushBuffer();
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("No permission.");
            }            
        }
        catch (FileUploadBase.SizeLimitExceededException ex){
            response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
            response.getWriter().write("The file is too large, max size: "
                    + String.format("%.2f", FileStore.getMaxSizeOfUploadingFile() / 1024.0 / 1024.0)
                    + " Mb.");
        }catch (Exception ex){
            Logger.log.error("File uploading error.", ex.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private List<FileItem> extractFileItems(HttpServletRequest request) throws ServletException, FileUploadException
    {
        if(!ServletFileUpload.isMultipartContent(request)) {
            throw new ServletException();
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!        
        //factory.setSizeThreshold(1024*1024);
        //java.io.File tempDir = (java.io.File)getServletContext().getAttribute("javax.servlet.context.tempdir");
        //factory.setRepository(tempDir);

        ServletFileUpload uploader = new ServletFileUpload(factory);
        uploader.setHeaderEncoding(StandardCharsets.UTF_8.name());
        uploader.setSizeMax(FileStore.getMaxSizeOfUploadingFile());

        return uploader.parseRequest(request);
    }
    
    private java.io.File uploadFile(FileItem fileItem) throws IOException, Exception
    {
        java.io.File uploadingFile = null;
        
        if (!fileItem.isFormField())
        {
            java.io.File path = new java.io.File(FileStore.getFilesDirectory() + java.io.File.separator + "temp");
            if(!path.exists()){
                path.mkdir();
            }
            
            do{
                String fileName = path + java.io.File.separator + random.nextInt();
                uploadingFile = new java.io.File(fileName);
            }while(uploadingFile.exists());

            uploadingFile.createNewFile();
            fileItem.write(uploadingFile);
        }
        
        return uploadingFile;
    }
}
  
