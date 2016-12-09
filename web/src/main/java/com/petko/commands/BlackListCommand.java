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

public class BlackListCommand extends AbstractCommand{
    private static BlackListCommand instance;

    @Autowired
    private ResourceManager resourceManager;

    private BlackListCommand() {}

    public static synchronized BlackListCommand getInstance() {
        if (instance == null) {
            instance = new BlackListCommand();
        }
        return instance;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(/*request,*/ login)) {
            String page = resourceManager.getProperty(Constants.PAGE_BLACK_LIST);
            List<UsersEntity> blackList = userService.getUsersByBlock(request, true);
            session.setAttribute("blackList", blackList);
            setForwardPage(request, page);
        // если не админ, сообщаем о невозможности выполнения команды
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
