package com.petko;

import static org.mockito.Mockito.*;

import com.petko.dao.*;
import com.petko.entities.*;
import com.petko.services.IOrderService;
import com.petko.vo.FullOrdersList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ContextConfiguration("/testContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class OrderServiceTest {
    @Autowired
    public IOrderDao orderDao;
    @Autowired
    public IOrderService orderService;
    @Autowired
    public IUserDao userDao;
    @Autowired
    public IBookDao bookDao;

    public static HttpServletRequest request;
    public static ModelMap modelMap;

    @BeforeClass
    public static void init() {
//        orderService = OrderService.getInstance();
//        orderDao = OrderDao.getInstance();
        request = mock(HttpServletRequest.class);
        modelMap = mock(ModelMap.class);
    }

    private int getTheLastOrderId() throws DaoException {
        List<OrdersEntity> list = orderDao.getAbsolutelyAll();
        Set<Integer> orderIds = list.stream().map(OrdersEntity::getOrderId).collect(Collectors.toSet());
        Object[] ids = orderIds.toArray();
        Arrays.sort(ids);
        return (int) ids[ids.length - 1];
    }

    private OrdersEntity saveAndGetNewOrderEntity() throws DaoException {
        OrdersEntity orderEntity = new OrdersEntity();
        orderEntity.setLogin("who");
        orderEntity.setBookId(1);
        orderEntity.setStatus(OrderStatus.ON_HAND.toString());
        orderEntity.setPlaceOfIssue(PlaceOfIssue.HOME.toString());
        orderEntity.setStartDate(new Date());
        orderEntity.setEndDate(new Date());
        orderDao.save(orderEntity);
        int pastId = getTheLastOrderId();
        return orderDao.getById(pastId);
    }

    @Test
    public void testGetOrdersByLoginAndStatus1() {
        orderService.getOrdersByLoginAndStatus(null, null);
    }

    @Test
    public void testGetOrdersByLoginAndStatus2() {
        List<FullOrdersList> list = orderService.getOrdersByLoginAndStatus(null, null);
        Assert.assertNotNull(list);
    }

    @Test
    public void testGetOrdersByLoginAndStatus3() throws DaoException {
        UsersEntity user = userDao.getById(2);
        List<FullOrdersList> list = orderService.getOrdersByLoginAndStatus(user.getLogin(), OrderStatus.CLOSED);
        Assert.assertTrue(!list.isEmpty());
    }

    @Test
    public void testGetExpiredOrders1() {
        List<FullOrdersList> list = orderService.getExpiredOrders();
        Assert.assertNotNull(list);
    }

    @Test(expected = NullPointerException.class)
    public void testCloseOrder1() {
        orderService.closeOrder(null, -10_000);
    }

    @Test
    public void testCloseOrder2() throws DaoException {
        OrdersEntity ordersEntity = saveAndGetNewOrderEntity();
        orderService.closeOrder(null, ordersEntity.getOrderId());
    }

    @Test
    public void testCloseOrder3() throws DaoException {
        OrdersEntity ordersEntity = saveAndGetNewOrderEntity();
        ordersEntity.setStatus(OrderStatus.ORDERED.toString());
        orderService.closeOrder(ordersEntity.getLogin(), ordersEntity.getOrderId());
    }

    @Test(expected = NullPointerException.class)
    public void testOrderToHomeOrToRoom1() {
        orderService.orderToHomeOrToRoom(null, null, -10_000, null);
    }

    @Test
    public void testOrderToHomeOrToRoom2() {
        orderService.orderToHomeOrToRoom(modelMap, null, -10_000, null);
    }

    @Test
    public void testOrderToHomeOrToRoom3() {
        orderService.orderToHomeOrToRoom(modelMap, null, -10_000, true);
    }

    @Test
    public void testOrderToHomeOrToRoom4() throws DaoException {
        int startCountOfOrders = getTheLastOrderId();
        int bookId = 0;
        List<BooksEntity> bookList = bookDao.getAbsolutelyAll();
        for (BooksEntity book : bookList) {
            if (!book.getIsBusy()) {
                bookId = book.getBookId();
                break;
            }
        }
        String userLogin = "";
        List<UsersEntity> userList = userDao.getAbsolutelyAll();
        for (UsersEntity userTemp : userList) {
            if (!userTemp.getIsBlocked() && !userTemp.getIsAdmin()) {
                userLogin = userTemp.getLogin();
                break;
            }
        }
        orderService.orderToHomeOrToRoom(modelMap, userLogin, bookId, true);
        int endCountOfOrders = getTheLastOrderId();
        if (startCountOfOrders != endCountOfOrders) orderService.closeOrder(null, getTheLastOrderId());
    }

    @Test
    public void testProlongOrder1() {
        orderService.prolongOrder(null, null, -10_000);
    }

    @Test
    public void testProlongOrder2() {
        orderService.prolongOrder(modelMap, null, -10_000);
    }

    @Test
    public void testProlongOrder3() throws DaoException {
        OrdersEntity orderEntity = saveAndGetNewOrderEntity();
        orderService.prolongOrder(modelMap, orderEntity.getLogin(), orderEntity.getOrderId());
        orderService.closeOrder(null, orderEntity.getOrderId());
    }

    @Test(expected = NullPointerException.class)
    public void testProvideBook1() {
        orderService.provideBook(null, -10_000);
    }

    @Test
    public void testProvideBook2() {
        orderService.provideBook(modelMap, -10_000);
    }

    @Test
    public void testProvideBook3() throws DaoException {
        OrdersEntity orderEntity = saveAndGetNewOrderEntity();
        String userLogin = "";
        List<UsersEntity> userList = userDao.getAbsolutelyAll();
        for (UsersEntity userTemp : userList) {
            if (!userTemp.getIsBlocked() && !userTemp.getIsAdmin()) {
                userLogin = userTemp.getLogin();
                break;
            }
        }
        int bookId = 0;
        List<BooksEntity> bookList = bookDao.getAbsolutelyAll();
        for (BooksEntity book : bookList) {
            if (!book.getIsBusy()) {
                bookId = book.getBookId();
                break;
            }
        }
        orderEntity.setLogin(userLogin);
        orderEntity.setBookId(bookId);
        orderEntity.setStatus(OrderStatus.ORDERED.toString());
        orderService.provideBook(modelMap, orderEntity.getOrderId());
        orderService.closeOrder(null, orderEntity.getOrderId());
    }

    @Test
    public void testCreateNewEntity1() {
        orderService.createNewEntity(null, -10_000, null, null, null, null);
    }

    @Test
    public void testGetById1() {
        OrdersEntity entity = orderService.getById(-10_000);
        Assert.assertNull(entity);
    }

    @Test
    public void testGetById2() {
        OrdersEntity entity = orderService.getById(-10_000);
        Assert.assertNull(entity);
    }
}
