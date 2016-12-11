package com.petko.services;

import com.petko.entities.OrderStatus;
import com.petko.entities.OrdersEntity;
import com.petko.vo.FullOrdersList;
import org.springframework.ui.ModelMap;

import java.util.Date;
import java.util.List;

public interface IOrderService {
    void closeOrder(String login, int orderID);

    OrdersEntity getById(int orderID);

    List<FullOrdersList> getExpiredOrders();

    List<FullOrdersList> getOrdersByLoginAndStatus(String login, OrderStatus orderStatus);

    void orderToHomeOrToRoom(ModelMap modelMap, String login, int bookID, Boolean isToHome);

    void prolongOrder(ModelMap modelMap, String login, int orderID);

    void provideBook(ModelMap modelMap, int orderID);

    OrdersEntity createNewEntity(String login, int bookId, String status, String place, Date startDate, Date endDate);
}
