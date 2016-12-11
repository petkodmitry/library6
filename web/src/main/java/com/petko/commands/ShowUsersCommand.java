package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.UsersEntity;
import com.petko.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class ShowUsersCommand extends AbstractCommand {
    @Autowired
    private ResourceManager resourceManager;

    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        // если админ, то выполняем команду
        if (userService.isAdminUser(/*request,*/ login)) {
            List<UsersEntity> userSet = userService.getAll(null, session);
            if (userSet.isEmpty()) setErrorMessage(request, "Не удалось получить список пользователей");
            request.setAttribute(Constants.USER_SET, userSet);
            String page = resourceManager.getProperty(Constants.PAGE_SHOW_USERS);
            setForwardPage(request, page);
        // если не админ, сообщаем о невозможности выполнения команды
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
