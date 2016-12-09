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
import java.util.List;

public class AdminSeminarsCommand extends AbstractCommand {
    private static AdminSeminarsCommand instance;

    @Autowired
    private ResourceManager resourceManager;

    private AdminSeminarsCommand() {}

    public static synchronized AdminSeminarsCommand getInstance() {
        if (instance == null) {
            instance = new AdminSeminarsCommand();
        }
        return instance;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        String page = resourceManager.getProperty(Constants.PAGE_ADMIN_SEMINARS);

        if (userService.isAdminUser(/*request,*/ login)) {
            List<SeminarsEntity> allSeminars = seminarService.getAll(request);
            session.setAttribute("allSeminars", allSeminars);
            setForwardPage(request, page);
        // если не админ, сообщаем о невозможности выполнения команды
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
