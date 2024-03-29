/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.filestore.dao;

import com.j256.ormlite.dao.Dao;
import com.qnium.common.backend.core.EntityManager;
import com.qnium.common.filestore.FileStore;
import com.qnium.common.filestore.dataobjects.FileRecord;
import eu.medsea.util.MimeUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Drozhzhin Dmitry
 */
public class FileManager
{
    private static FileManager instance;    
    private final Dao<FileRecord, Long> fileDao;
    private final Random random = new Random();
    
    public static synchronized FileManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }
    
    private FileManager() throws SQLException
    {
        fileDao = EntityManager.getInstance(FileRecord.class).getDao();

        java.io.File file = new java.io.File(FileStore.getFilesDirectory());
        if(!file.exists()){
            file.mkdir();
        }
    }
    
    public FileRecord getFileRecord(long fileRecordId) throws SQLException{
        return fileDao.queryForEq(FileRecord.ID, fileRecordId).get(0);
    }
    
    public void setPublic(long fileRecordId, boolean viewForAll) throws SQLException
    {        
        FileRecord file = fileDao.queryForEq(FileRecord.ID, fileRecordId).get(0);
        
        if(file.viewForAll != viewForAll)
        {
            String newFileName = makeFileName(fileRecordId, viewForAll, getFileExtension(file.internalFileName));
            
            new java.io.File(makeFilePath(file.internalFileName)).renameTo(new java.io.File(makeFilePath(newFileName)));
            
            file.viewForAll = viewForAll;
            file.internalFileName = newFileName;
            fileDao.update(file);            
        }
    }
    
    public FileRecord createFile(byte[] data, long account, String fileExtension) throws SQLException, IOException
    {
        FileRecord file = new FileRecord();
        
        fileDao.create(file);
        
        String fileName = makeFileName(file.id, file.viewForAll, fileExtension);
        
        try (java.io.FileOutputStream fileStream = new FileOutputStream(new java.io.File(makeFilePath(fileName)))) {
            fileStream.write(data);
            fileStream.flush();
        }
        
        file.internalFileName = fileName;
        file.type = getMimeType(makeFilePath(fileName));
        fileDao.update(file);
        
        return file;
    }
    
    public void updateOriginalFileName(long fileRecordId, String newName) throws SQLException{
        FileRecord fileRecord = fileDao.queryForEq(FileRecord.ID, fileRecordId).get(0);
        fileRecord.originalFileName = newName;
        fileDao.update(fileRecord);
    }
    
    public byte[] readFile(long fileRecordId) throws SQLException, FileNotFoundException, IOException
    {
        FileRecord file = fileDao.queryForEq(FileRecord.ID, fileRecordId).get(0);
        
        String filePath = makeFilePath(file.internalFileName);
        
        byte[] data;
        try (FileInputStream fileStream = new java.io.FileInputStream(new java.io.File(filePath))) {
            data = new byte[(int)fileStream.getChannel().size()];
            fileStream.read(data);
        }
        
        return data;
    }
    
    public java.io.File getFile(long fileRecordId) throws SQLException
    {
        FileRecord file = fileDao.queryForEq(FileRecord.ID, fileRecordId).get(0);
        String filePath = makeFilePath(file.internalFileName);
        return new java.io.File(filePath);
    }
    
    public void deleteFile(long fileRecordId) throws SQLException
    {
        FileRecord fileInBase = fileDao.queryForEq(FileRecord.ID, fileRecordId).get(0);
        String filePath = makeFilePath(fileInBase.internalFileName);
        java.io.File file = new java.io.File(filePath);
        file.delete();
        fileDao.delete(fileInBase);
    }
    
    public FileRecord createUploadedFile(java.io.File uploadedFile, String originalFileName) throws SQLException, IOException
    {
        String fileExtension = getFileExtension(originalFileName);
        
        FileRecord fileRecord = new FileRecord();

        fileDao.create(fileRecord);
        
        String targetFileName = makeFileName(fileRecord.id, fileRecord.viewForAll, fileExtension);
        java.io.File targetFile = new java.io.File(makeFilePath(targetFileName));
        if(targetFile.exists()) {
            targetFile.delete();
        }
        uploadedFile.renameTo(targetFile);
        
        fileRecord.internalFileName = targetFileName;
        fileRecord.type = getMimeType(makeFilePath(targetFileName));
        fileRecord.originalFileName = originalFileName;
        fileDao.update(fileRecord);
        
        return fileRecord;
    }    
    
    private String makeFilePath(String fileName)
    {
        StringBuilder filePath = new StringBuilder(FileStore.getFilesDirectory());
        filePath.append(java.io.File.separator);
        filePath.append(fileName);
        
        return filePath.toString();
    }
    
    private String makeFileName(long fileId, boolean viewForAll, String fileExtension)
    {
        StringBuilder fileName = new StringBuilder();
        fileName.append(viewForAll ? "public" : "private");
        fileName.append('_').append(Long.toString(fileId));
        fileName.append('.').append(fileExtension);
        
        return fileName.toString();
    }
    
    private static String getFileExtension(String fileName)
    {
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
    
    private static String getMimeType(String filePath) throws java.io.IOException, MalformedURLException
    {
        return MimeUtil.getMimeType(filePath);
    }
    
    public FileRecord createFile(InputStream stream, String fileName) throws IOException, SQLException
    {
        File tempFile = createTempFile();
        Files.copy(stream, tempFile.toPath(), REPLACE_EXISTING);
        FileRecord fileRecord = createUploadedFile(tempFile, fileName);
        return fileRecord;
    }
    
    public File createTempFile() throws IOException
    {
        File tempFile;
        
        File path = new File(FileStore.getFilesDirectory() + File.separator + "temp");
        
        if(!path.exists()){
            path.mkdir();
        }

        do{
            String fileName = path + File.separator + random.nextInt();
            tempFile = new File(fileName);
        }while(tempFile.exists());

        tempFile.createNewFile();
        
        return tempFile;
    }
    
    public List<FileRecord> getFileRecords(List<Long> fileRecordIds) throws SQLException
    {
        return fileDao.queryBuilder().where().in(FileRecord.ID, fileRecordIds).query();
    }
}
