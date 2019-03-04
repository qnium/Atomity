/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.webrunner;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author admin
 */
public class TemplateEngine {
    
    private TemplateEngine() {
    }
    
    public static TemplateEngine getInstance() {
        return TemplateEngineHolder.INSTANCE;
    }
    
    private static class TemplateEngineHolder {

        private static final TemplateEngine INSTANCE = new TemplateEngine();
    }
    
    public Document load(String path) throws IOException
    {
        File template = new File(Router.getInstance().getStaticPath() + path);
        Document doc = Jsoup.parse(template, "UTF-8", "");
        doc.head().children().first().before("<base href=\"" + Router.getInstance().getBasePath() + "\"/>");
        return doc;
    }
}
