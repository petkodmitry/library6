package com.petko.commands;

import com.petko.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.OrderStatus;
import com.petko.services.OrderService;
import com.petko.vo.FullOrdersList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class MyOrdersCommand extends AbstractCommand{
    private static MyOrdersCommand instance;

    private MyOrdersCommand() {
    }

    public static synchronized MyOrdersCommand getInstance() {
        if (instance == null) {
            instance = new MyOrdersCommand();
        }
        return instance;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        OrderService service = OrderService.getInstance();
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        String page = ResourceManager.getInstance().getProperty(Constants.PAGE_MY_ORDERS);
        List<FullOrdersList> myOrdersList;
        myOrdersList = service.getOrdersByLoginAndStatus(request, login, OrderStatus.ORDERED);
        request.setAttribute("myOrdersList", myOrdersList);

        setForwardPage(request, page);
    }
}
