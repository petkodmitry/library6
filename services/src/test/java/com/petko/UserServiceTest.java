package com.petko;

import com.petko.dao.IUserDao;
import com.petko.dao.UserDao;
import com.petko.entities.UsersEntity;
import com.petko.services.IUserService;
import com.petko.services.UserService;
import static org.mockito.Mockito.*;
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
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ContextConfiguration("/testContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UserServiceTest {
//    public static UserService userService;
    @Autowired
    public IUserDao userDao;
    @Autowired
    public IUserService userService;
    public static HttpServletRequest request;
    public static HttpSession session;
    public static ModelMap modelMap;

    @BeforeClass
    public static void init() {
//        userService = UserService.getInstance();
//        userDao = UserDao.getInstance();
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        modelMap = mock(ModelMap.class);
    }

    public int getTheLastUserId() throws DaoException {
        List<UsersEntity> list = userDao.getAbsolutelyAll();
        Set<Integer> userIds = list.stream().map(UsersEntity::getUserId).collect(Collectors.toSet());
        Object[] ids = userIds.toArray();
        Arrays.sort(ids);
        return (int) ids[ids.length - 1];
    }

    @Test
    public void testAdd1() {
        userService.add(null);
    }

    @Test
    public void testAdd2() throws DaoException {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setFirstName("test");
        usersEntity.setLastName("test");
        usersEntity.setLogin("test");
        usersEntity.setPassword("123456789");
        usersEntity.setIsAdmin(false);
        usersEntity.setIsBlocked(false);
        usersEntity.setSeminars(new HashSet<>());
        userService.add(usersEntity);
        int id = getTheLastUserId();
        usersEntity = userDao.getById(id);
        userService.setBlockUser(usersEntity.getLogin(), true);
        userService.deleteUser(request, id);
    }

    @Test(expected = NullPointerException.class)
    public void testGetAll1() {
        userService.getAll(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetAll2() {
        List<UsersEntity> list = userService.getAll(modelMap, session);
        Assert.assertNull(list);
    }

    @Test (expected = NullPointerException.class)
    public void testGetAll3() {
        userService.getAll(modelMap, session);
    }

    @Test (expected = NullPointerException.class)
    public void testLogOut1() {
        userService.logOut(null, null);
    }

    @Test
    public void testIsLoginSuccess1() throws DaoException {
        boolean result = userService.isLoginSuccess(null, null);
        Assert.assertTrue(!result);
    }

    @Test
    public void testIsLoginSuccess2() throws DaoException {
        boolean result = userService.isLoginSuccess("1", "2");
        Assert.assertTrue(!result);
    }

    @Test
    public void testIsLoginSuccess3() throws DaoException {
        int userId = getTheLastUserId();
        UsersEntity entity = userDao.getById(userId);
        boolean result = userService.isLoginSuccess(entity.getLogin(), entity.getPassword());
        Assert.assertTrue(result);
    }

    @Test
    public void testIsAdminUser1() {
        boolean result = userService.isAdminUser(null);
        Assert.assertTrue(!result);
    }

    @Test
    public void testIsAdminUser2() throws DaoException {
        int userId = getTheLastUserId();
        UsersEntity entity = userDao.getById(userId);
        boolean result = userService.isAdminUser(entity.getLogin());
        Assert.assertTrue(!result);
    }

    @Test
    public void testIsLoginExists1() {
        boolean result = userService.isLoginExists( null);
        Assert.assertTrue(!result);
    }

    @Test
    public void testIsLoginExists2() throws DaoException {
        int userId = getTheLastUserId();
        UsersEntity entity = userDao.getById(userId);
        boolean result = userService.isLoginExists(entity.getLogin());
        Assert.assertTrue(result);
    }

    @Test
    public void testIsAllPasswordRulesFollowed1() {
        boolean result = userService.isAllPasswordRulesFollowed(null, null);
        Assert.assertTrue(!result);
    }

    @Test
    public void testIsAllPasswordRulesFollowed2() {
        boolean result = userService.isAllPasswordRulesFollowed("1", "2");
        Assert.assertTrue(!result);
    }

    @Test
    public void testIsAllPasswordRulesFollowed3() {
        boolean result = userService.isAllPasswordRulesFollowed("123456789", "123456789");
        Assert.assertTrue(result);
    }

    @Test
    public void testSetBlockUser1() {
        userService.setBlockUser(null, false);
    }

    @Test
    public void testGetUsersByBlock() {
        List<UsersEntity> list = userService.getUsersByBlock(false);
        Assert.assertTrue(list != null);
    }

    @Test
    public void testIsAllRegisterDataEntered1() {
        boolean result = userService.isAllRegisterDataEntered(null, null);
        Assert.assertTrue(!result);
    }

    @Test
    public void testIsAllRegisterDataEntered2() {
        boolean result = userService.isAllRegisterDataEntered(new UsersEntity(), null);
        Assert.assertTrue(!result);
    }

    @Test
    public void testIsAllRegisterDataEntered3() {
        UsersEntity entity = new UsersEntity();
        entity.setFirstName("f");
        entity.setLastName("l");
        entity.setLogin("login");
        entity.setPassword("psw");
        boolean result = userService.isAllRegisterDataEntered(entity, null);
        Assert.assertTrue(!result);
    }

    @Test
    public void testIsAllRegisterDataEntered4() {
        UsersEntity entity = new UsersEntity();
        entity.setFirstName("f");
        entity.setLastName("l");
        entity.setLogin("login");
        entity.setPassword("psw");
        boolean result = userService.isAllRegisterDataEntered(entity, "psw1");
        Assert.assertTrue(result);
    }

    @Test
    public void testIsAllRegisterDataEntered5() {
        UsersEntity entity = new UsersEntity();
        entity.setFirstName("f");
        entity.setLastName("l");
        entity.setLogin("login");
        entity.setPassword("123456789");
        boolean result = userService.isAllRegisterDataEntered(entity, "123456789");
        Assert.assertTrue(result);
    }

    @Test
    public void testSetAllDataOfEntity1() {
        UsersEntity entity = new UsersEntity();
        entity = userService.setAllDataOfEntity(entity, null, null, null, null, false, false);
        Assert.assertTrue(entity.getFirstName() == null && entity.getLastName() == null &&
                entity.getLogin() == null && entity.getPassword() == null &&
                !entity.getIsAdmin() && !entity.getIsBlocked());
    }

    @Test
    public void testSetAllDataOfEntity2() {
        UsersEntity entity = new UsersEntity();
        entity = userService.setAllDataOfEntity(entity, "f", "l", "login", "psw", true, true);
        Assert.assertTrue(entity.getFirstName().equals("f") && entity.getLastName().equals("l") &&
                entity.getLogin().equals("login") && entity.getPassword().equals("psw") &&
                entity.getIsAdmin() && entity.getIsBlocked());
    }

    @Test
    public void testActiveUsers() {
        ActiveUsers.addUser("testLogin");
        userService.logOut(session, "testLogin");
    }
}
