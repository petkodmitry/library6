package com.petko.commands;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ProlongOrderCommand extends AbstractCommand{
    @Autowired
    private MyBooksCommand myBooksCommand;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        Integer orderId = Integer.parseInt(request.getParameter("orderId"));
        orderService.prolongOrder(null, login, orderId);

        myBooksCommand.execute(request, response);
    }
}
