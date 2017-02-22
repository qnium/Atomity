/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.filestore.dataobjects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author Drozhzhin Dmitry
 */
@DatabaseTable(tableName = "files")
public class FileRecord
{    
    public static final String ID = "id";
    @DatabaseField(columnName = ID, generatedId = true)
    public long id;
    
    public static final String TYPE = "type";
    @DatabaseField(columnName = TYPE)
    public String type;
    
    public static final String VIEW_FOR_ALL = "view_for_all";
    @DatabaseField(columnName = VIEW_FOR_ALL)
    public boolean viewForAll;
    
    public static final String INTERNAL_FILE_NAME = "internal_file_name";
    @DatabaseField(columnName = INTERNAL_FILE_NAME)
    public String internalFileName;
    
    public static final String ORIGINAL_FILE_NAME = "original_file_name";
    @DatabaseField(columnName = ORIGINAL_FILE_NAME)
    public String originalFileName;
}
