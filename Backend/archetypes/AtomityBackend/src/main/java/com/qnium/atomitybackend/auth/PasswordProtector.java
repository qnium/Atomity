/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Ivan
 */
public class PasswordProtector {
    
    public static boolean checkPassword(String plaintextPassword, String protectedPassword) {
        return BCrypt.checkpw(plaintextPassword, protectedPassword);
    }
    
    public static String protectPassword(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }
}
