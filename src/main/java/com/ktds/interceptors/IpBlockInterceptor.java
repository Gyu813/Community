package com.ktds.interceptors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class IpBlockInterceptor extends HandlerInterceptorAdapter {

	private List<String> blackList;
	
	public IpBlockInterceptor() {
		blackList = new ArrayList<String>();
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object controller)
			throws Exception {
		
		String requestorIp = request.getRemoteAddr();
		if ( blackList.contains(requestorIp) ) {
			//RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/view/error/403.jsp");
			//rd.forward(request, response);
			//return false;
		}
		
		return true;
	}
}
