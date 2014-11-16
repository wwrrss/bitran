/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bitran.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import javax.servlet.ServletRegistration.Dynamic;
/**
 *
 * @author Willian
 */
public class Inicializador implements WebApplicationInitializer{

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.scan("com.bitran");
        sc.addListener(new ContextLoaderListener(ctx));
        Dynamic servlet = sc.addServlet("appServlet", new DispatcherServlet(ctx));
        servlet.setAsyncSupported(true);
        servlet.setLoadOnStartup(1);
        servlet.addMapping("*.action");
    }
    
}
