package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.SeminarsEntity;
import com.petko.services.SeminarService;
import com.petko.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminSeminarsCommand extends AbstractCommand {
    @Autowired
    private ResourceManager resourceManager;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        String page = resourceManager.getProperty(Constants.PAGE_ADMIN_SEMINARS);

        if (userService.isAdminUser(/*request,*/ login)) {
            List<SeminarsEntity> allSeminars = seminarService.getAll();
            session.setAttribute("allSeminars", allSeminars);
            setForwardPage(request, page);
        // если не админ, сообщаем о невозможности выполнения команды
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
