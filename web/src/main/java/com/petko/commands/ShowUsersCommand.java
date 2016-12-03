package com.petko.commands;

import com.petko.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.UsersEntity;
import com.petko.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class ShowUsersCommand extends AbstractCommand {
    private static ShowUsersCommand instance;

    private ShowUsersCommand() {}

    public static synchronized ShowUsersCommand getInstance() {
        if(instance == null){
            instance = new ShowUsersCommand();
        }
        return instance;
    }

    public void execute(HttpServletRequest request, HttpServletResponse response) {
        UserService service = UserService.getInstance();
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        // если админ, то выполняем команду
        if (service.isAdminUser(request, login)) {
            List<UsersEntity> userSet = service.getAll(request);
            if (userSet.isEmpty()) setErrorMessage(request, "Не удалось получить список пользователей");
            request.setAttribute(Constants.USER_SET, userSet);
            String page = ResourceManager.getInstance().getProperty(Constants.PAGE_SHOW_USERS);
            setForwardPage(request, page);
        // если не админ, сообщаем о невозможности выполнения команды
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
