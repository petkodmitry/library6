package com.petko.commands;

import com.petko.constants.Constants;
import com.petko.services.IUserService;
import com.petko.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@Controller
public class LoginCommand extends AbstractCommand {
    @Autowired
    private IUserService userService;
    private LoginCommand() {}

    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        // deleting all session attributes, except "user"
        Enumeration<String> attributesNames = session.getAttributeNames();
        while (attributesNames.hasMoreElements()) {
            String attr = attributesNames.nextElement();
            if (!"user".equals(attr)) session.removeAttribute(attr);
        }

        String login = request.getParameter("login");
        String password = request.getParameter("password");
        if (session.getAttribute("user") != null) {
            login = (String) session.getAttribute("user");
            redirectToMainPage(request, login);
        } else if (!"".equals(login) && userService.isLoginSuccess(request, login, password)) {
            session.setAttribute("user", login);
            redirectToMainPage(request, login);
        } else {
            if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null && login != null && !"".equals(login)) {
                setErrorMessage(request, "Неверный логин или пароль!");
            }
            redirectToLoginPage(request);
        }
    }
}
