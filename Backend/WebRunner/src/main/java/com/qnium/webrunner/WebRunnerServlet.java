/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.webrunner;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
        
        
/**
 *
 * @author admin
 */
public class WebRunnerServlet extends HttpServlet
{
    //process the application path
     @Override
     public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
     {
         String path = req.getPathInfo();
         resp.getWriter().println("Path is: " + path + "params: " + req.getParameterMap());
     }
}
