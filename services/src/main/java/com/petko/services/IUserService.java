package com.petko.services;

import com.petko.entities.UsersEntity;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public interface IUserService {
    boolean isLoginSuccess(String login, String password);

    boolean isAdminUser(String login);

    List<UsersEntity> getAll(ModelMap modelMap, HttpSession session);

    List<UsersEntity> getUsersByBlock(boolean isBlocked);

    void setBlockUser(String login, boolean isBlocked);

    void logOut(HttpSession session, String login);

    UsersEntity setAllDataOfEntity(UsersEntity result, String firstName, String lastName, String login, String password,
                                   boolean isAdmin, boolean isBlocked);

    boolean isLoginExists(String login);

    boolean isAllRegisterDataEntered (UsersEntity regData, String repeatPassword);

    boolean isAllPasswordRulesFollowed(String password, String repeatPassword);

    void add(UsersEntity entity);

    UsersEntity deleteUser(HttpServletRequest request, Integer userId);
}
