package com.petko.managers;

import com.petko.ActiveUsers;
import com.petko.commands.CommandType;
import com.petko.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

@Component
public class AuthorizationFilter implements Filter {
    @Autowired
    private ResourceManager resourceManager;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        HttpSession session = request.getSession();
//        CommandType commandType = CommandType.getCommandType(request.getParameter("cmd"));
//        if (commandType != CommandType.REGISTER &&
//                (!CommandType.LOGIN.equals(commandType) && !ActiveUsers.isUserActive((String) session.getAttribute("user")))) {
//            String page = resourceManager.getProperty(Constants.PAGE_INDEX);
//            RequestDispatcher dispatcher = request.getRequestDispatcher(page);
//            dispatcher.forward(request, response);
//            session.invalidate();
//        } else {
//            filterChain.doFilter(servletRequest, servletResponse);
//        }
//    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        String requestURI = request.getRequestURI();
        String command = requestURI.substring(requestURI.lastIndexOf("/") + 1);
//        CommandType commandType = CommandType.getCommandType(request.getParameter("cmd"));
//        if (commandType != CommandType.REGISTER &&
//                (!CommandType.LOGIN.equals(commandType) && !ActiveUsers.isUserActive((String) session.getAttribute("user")))) {
        if (!"register".equals(command.toLowerCase()) &&
                (!"login".equals(command.toLowerCase()) && !ActiveUsers.isUserActive((String) session.getAttribute("user")))) {
//            String page = resourceManager.getProperty(Constants.PAGE_INDEX);
//            RequestDispatcher dispatcher = request.getRequestDispatcher(page);
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
