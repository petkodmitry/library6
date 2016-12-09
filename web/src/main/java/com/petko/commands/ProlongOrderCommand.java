package com.petko.commands;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ProlongOrderCommand extends AbstractCommand{
    private static ProlongOrderCommand instance;

    private ProlongOrderCommand() {
    }

    public static synchronized ProlongOrderCommand getInstance() {
        if (instance == null) {
            instance = new ProlongOrderCommand();
        }
        return instance;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        Integer orderId = Integer.parseInt(request.getParameter("orderId"));
        orderService.prolongOrder(request, login, orderId);

        MyBooksCommand.getInstance().execute(request, response);
    }
}
