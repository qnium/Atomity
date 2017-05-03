/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.users.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qnium.atomitybackend.users.initializers.UsersInitializer;
import com.qnium.common.backend.assets.definitions.EntityInitializer;

/**
 *
 * @author Ivan
 */

@DatabaseTable(tableName = "users")
@EntityInitializer(entityInitializerClass = UsersInitializer.class)
public class User {
    
    public static final String ID = "id";
    @DatabaseField(generatedId = true, columnName = ID)
    public long id;
    
    public static final String EMAIL = "email";
    @DatabaseField(columnName = EMAIL, unique = true)
    public String email;
    
    public static final String PASSWORD = "password";
    @DatabaseField(columnName = PASSWORD)
    public String password;
    
    public static final String VERIFICATION_CODE = "verification_code";
    @DatabaseField(columnName = VERIFICATION_CODE)
    public String verificationCode;
}
