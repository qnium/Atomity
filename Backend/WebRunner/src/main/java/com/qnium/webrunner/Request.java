/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.webrunner;

/**
 *
 * @author admin
 */
public class Request {
    public String path;
    public String query;
    
    public Request(String path) {
        this(path, null);
    }
    
    public Request(String path, String query) {
        this.path = path;
        this.query = query;
    }
}
