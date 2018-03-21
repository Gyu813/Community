package com.ktds.interceptors;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AccessDenyInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		HttpSession session = request.getSession();
		Map<String, Integer> accessDenyMap = new HashMap<>();

		if (request.getMethod().equals("POST")) {
			try {
				if (session.getAttribute("status").equals("fail")) {
					String failId = request.getParameter("id");
					if (accessDenyMap.containsKey(failId)) {
						int count = accessDenyMap.get(failId);
						count++;
						accessDenyMap.put(failId, count);
						if (accessDenyMap.get(failId) > 3) {
							RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/view/error/accessDeny.jsp");
							rd.forward(request, response);
							return false;
						}
					} else {
						accessDenyMap.put(failId, 0);
					}
				}
			} catch (NullPointerException ne) {
				return true;
			}
		}

		return true;
	}
}
