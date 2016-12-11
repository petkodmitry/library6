package com.petko.commands;

import com.petko.constants.Constants;
import com.petko.entities.OrderStatus;
import com.petko.services.OrderService;
import com.petko.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CloseOrderCommand extends AbstractCommand{
    @Autowired
    private WaitingOrdersCommand waitingOrdersCommand;
    @Autowired
    private OpenedOrdersCommand openedOrdersCommand;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");

        if (userService.isAdminUser(/*request,*/ login)) {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String status = orderService.getById(orderId).getStatus();
            orderService.closeOrder(null, orderId);
            if (OrderStatus.ORDERED.toString().equals(status)) {
                waitingOrdersCommand.execute(request, response);
            } else if (OrderStatus.ON_HAND.toString().equals(status)) {
                openedOrdersCommand.execute(request, response);
            }
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
