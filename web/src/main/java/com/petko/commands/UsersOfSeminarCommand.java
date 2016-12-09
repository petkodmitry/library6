package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.SeminarsEntity;
import com.petko.services.SeminarService;
import com.petko.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UsersOfSeminarCommand extends AbstractCommand {
    private static UsersOfSeminarCommand instance;
    @Autowired
    private ResourceManager resourceManager;

    private UsersOfSeminarCommand() {}

    public static synchronized UsersOfSeminarCommand getInstance() {
        if (instance == null) {
            instance = new UsersOfSeminarCommand();
        }
        return instance;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        String page = resourceManager.getProperty(Constants.PAGE_ADMIN_SEMINARS);

        if (userService.isAdminUser(/*request,*/ login)) {
            Integer seminarId = Integer.parseInt(request.getParameter("seminarId"));
            SeminarsEntity seminarEntity = seminarService.getById(request, seminarId);
            session.setAttribute("seminarEntity", seminarEntity);
            setForwardPage(request, page);
        // если не админ, сообщаем о невозможности выполнения команды
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
