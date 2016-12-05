package com.petko.services;

import com.petko.entities.OrderStatus;
import com.petko.entities.OrdersEntity;
import com.petko.vo.FullOrdersList;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

public interface IOrderService {
    void closeOrder(HttpServletRequest request, String login, int orderID);

    OrdersEntity getById(HttpServletRequest request, int orderID);

    List<FullOrdersList> getExpiredOrders(HttpServletRequest request);

    List<FullOrdersList> getOrdersByLoginAndStatus(HttpServletRequest request, String login, OrderStatus orderStatus);

    void orderToHomeOrToRoom(HttpServletRequest request, String login, int bookID, Boolean isToHome);

    void prolongOrder(HttpServletRequest request, String login, int orderID);

    void provideBook(HttpServletRequest request, int orderID);

    OrdersEntity createNewEntity(String login, int bookId, String status, String place, Date startDate, Date endDate);
}
