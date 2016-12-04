package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.services.IUserService;
import com.petko.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

@Controller(value = "abstractCommand")
public abstract class AbstractCommand implements Command{
    @Autowired
    private IUserService userService;

    public void setErrorMessage(HttpServletRequest request, String message) {
        request.setAttribute(errorMessageAttribute, message);
    }

    public void setForwardPage(HttpServletRequest request, String page) {
        request.setAttribute(forwardPageAttribute, page);
    }

    /*protected*/ public void redirectToMainPage(HttpServletRequest request, String login) {
        String page;
        if (userService.isAdminUser(request, login)) {
            page = ResourceManager.getInstance().getProperty(Constants.PAGE_MAIN_ADMIN);
        } else {
            page = ResourceManager.getInstance().getProperty(Constants.PAGE_MAIN);
        }
        setForwardPage(request, page);
    }

    protected void redirectToLoginPage(HttpServletRequest request) {
        String page = ResourceManager.getInstance().getProperty(Constants.PAGE_INDEX);
        setForwardPage(request, page);
    }
}
