package com.mycompany.matdongsan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

public class ViewResolverConfig {
	   @Bean //리턴된 객체를 관리객체로 만들어줌
	   public ViewResolver internalResourceViewResolver() {
	      InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
	      viewResolver.setViewClass(JstlView.class);
	      viewResolver.setPrefix("/WEB-INF/views/");
	      viewResolver.setSuffix(".jsp");
	      return viewResolver;
	   }
	}