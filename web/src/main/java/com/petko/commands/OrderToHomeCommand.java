package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class OrderToHomeCommand extends AbstractCommand {
    @Autowired
    private ResourceManager resourceManager;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        int bookId = Integer.parseInt(request.getParameter("bookId"));
//        orderService.orderToHomeOrToRoom(request, login, bookId, true);

        String page = resourceManager.getProperty(Constants.PAGE_SEARCH_BOOK_FOR_USER);
        setForwardPage(request, page);
    }
}
