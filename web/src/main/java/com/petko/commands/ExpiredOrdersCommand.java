package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.services.OrderService;
import com.petko.services.UserService;
import com.petko.vo.FullOrdersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ExpiredOrdersCommand extends AbstractCommand{
    @Autowired
    private ResourceManager resourceManager;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(/*request,*/ login)) {
            String page = resourceManager.getProperty(Constants.PAGE_EXPIRED_ORDERS);
            List<FullOrdersList> expiredOrdersList = orderService.getExpiredOrders();
            session.setAttribute("expiredOrdersList", expiredOrdersList);
            setForwardPage(request, page);
        // если не админ, сообщаем о невозможности выполнения команды
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
