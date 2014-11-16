/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bitran.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 *
 * @author Willian
 */
@Configuration
@EnableWebMvc

public class Config extends WebMvcConfigurerAdapter{
    @Bean
    public UrlBasedViewResolver setupViewResolver(){
        UrlBasedViewResolver ubvr = new UrlBasedViewResolver();
        ubvr.setPrefix("/WEB-INF/jsp/");
        ubvr.setSuffix(".jsp");
        ubvr.setViewClass(JstlView.class);
        return ubvr;
    }
    
}
