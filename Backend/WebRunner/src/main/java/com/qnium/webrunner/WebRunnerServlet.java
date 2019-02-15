/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.webrunner;
import com.qnium.webrunner.helpers.MediaTypes;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
        
        
/**
 *
 * @author admin
 */
public class WebRunnerServlet extends HttpServlet
{
    
     static final HashMap<String, String> TYPES = new HashMap();
     
     static {
         TYPES.put("js", MediaTypes.TEXT_JS);
         TYPES.put("css", MediaTypes.TEXT_CSS);
         TYPES.put("jpg", MediaTypes.IMAGE_JPEG);
         TYPES.put("jpeg", MediaTypes.IMAGE_JPEG);
         TYPES.put("png", MediaTypes.IMAGE_PNG);
         TYPES.put("html", MediaTypes.TEXT_HTML);
     }
    //process the application path
     @Override
     public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
     {
         if (!doGetStatic(Router.getInstance().getStaticPath(), req, resp))
         {
            String path = req.getPathInfo();
            Router.getInstance().processRoute(path, req.getParameterMap(), resp.getWriter());
         }
     }
     
     public boolean doGetStatic(String filePath, HttpServletRequest req, HttpServletResponse resp)
     {
         String ext = req.getPathInfo().substring(req.getPathInfo().lastIndexOf(".") + 1);
         
         String type = TYPES.get(ext);
         
         if (type == null)
             return false;
         
         resp.setStatus(HttpServletResponse.SC_OK);
         resp.setContentType(type);
         
         try {
             FileInputStream in = new FileInputStream(filePath + req.getPathInfo());
             resp.getOutputStream().write(in.readAllBytes());
         } catch (Exception ex) {
             Logger.getLogger(WebRunnerServlet.class.getName()).log(Level.SEVERE, null, ex);
             return false;
         }
         
         return true;
     }
}
