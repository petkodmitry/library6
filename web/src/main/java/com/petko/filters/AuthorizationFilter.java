package com.petko.filters;

import com.petko.ActiveUsers;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class AuthorizationFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        String requestURI = request.getRequestURI();
        String command = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        if (!"register".equals(command.toLowerCase()) &&
                (!"login".equals(command.toLowerCase()) && !ActiveUsers.isUserActive((String) session.getAttribute("user")))) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("login");
            dispatcher.forward(request, response);
            session.invalidate();
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy() {
    }
}
