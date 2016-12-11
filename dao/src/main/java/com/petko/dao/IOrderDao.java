package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.BooksEntity;
import com.petko.entities.OrderStatus;
import com.petko.entities.OrdersEntity;

import java.util.Date;
import java.util.List;

public interface IOrderDao extends Dao<OrdersEntity> {
    List<OrdersEntity> getOrdersByLoginAndStatus(String login, OrderStatus orderStatus) throws DaoException;

    List<OrdersEntity> getOrdersByStatusAndEndDate(OrderStatus orderStatus, Date endDate) throws DaoException;

    List<OrdersEntity> getOrdersByLoginBookIdStatuses(String login, BooksEntity bookEntity, String[] orderStatuses) throws DaoException;
}
