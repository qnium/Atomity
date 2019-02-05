/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.webrunner.interfaces;

import com.qnium.webrunner.Request;
import java.io.PrintWriter;

/**
 *
 * @author admin
 */
public interface IRouteHandler<T> {
    
    public void handle(Request request, T params, PrintWriter out);
    
}
