package com.petko.commands;

import com.petko.constants.Constants;
import com.petko.services.OrderService;
import com.petko.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ProvideBookCommand extends AbstractCommand {
    @Autowired
    private WaitingOrdersCommand waitingOrdersCommand;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(/*request,*/ login)) {
            Integer orderId = Integer.parseInt(request.getParameter("orderId"));
//            orderService.provideBook(request, orderId);
            waitingOrdersCommand.execute(request, response);
        // если не админ, сообщаем о невозможности выполнения команды
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
