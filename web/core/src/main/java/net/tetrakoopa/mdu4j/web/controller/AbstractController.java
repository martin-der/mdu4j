package net.tetrakoopa.mdu4j.web.controller;

//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AbstractController {
	
	protected WebApplicationContext getWebApplicationContext(HttpServletRequest request) {
		final HttpSession session = request.getSession();
		final ServletContext servletContext = session.getServletContext();
		return WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}
	

}
