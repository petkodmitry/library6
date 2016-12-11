package com.petko.commands;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CancelUserOrderCommand extends AbstractCommand{
    @Autowired
    private MyOrdersCommand myOrdersCommand;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        orderService.closeOrder(login, orderId);

        myOrdersCommand.execute(request, response);
    }
}
