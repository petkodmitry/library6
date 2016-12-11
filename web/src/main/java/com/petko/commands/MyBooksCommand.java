package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.OrderStatus;
import com.petko.services.OrderService;
import com.petko.vo.FullOrdersList;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class MyBooksCommand extends AbstractCommand{
    @Autowired
    private ResourceManager resourceManager;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        String page = resourceManager.getProperty(Constants.PAGE_MY_BOOKS);
        List<FullOrdersList> myBooksList;
        myBooksList = orderService.getOrdersByLoginAndStatus(login, OrderStatus.ON_HAND);
        session.setAttribute("myBooksList", myBooksList);

        setForwardPage(request, page);
    }
}
