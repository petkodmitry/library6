package com.petko.services;

import com.petko.entities.UsersEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IUserService {
    boolean isLoginSuccess(HttpServletRequest request, String login, String password);

    boolean isAdminUser(HttpServletRequest request, String login);

    List<UsersEntity> getAll(HttpServletRequest request);

    List<UsersEntity> getUsersByBlock(HttpServletRequest request, boolean isBlocked);

    void setBlockUser(HttpServletRequest request, String login, boolean isBlocked);

    void logOut(HttpServletRequest request, String login);

    UsersEntity setAllDataOfEntity(UsersEntity result, String firstName, String lastName, String login, String password,
                                   boolean isAdmin, boolean isBlocked);

    boolean isLoginExists(HttpServletRequest request, String login);

    boolean isAllRegisterDataEntered (UsersEntity regData, String repeatPassword);

    boolean isAllPasswordRulesFollowed(String password, String repeatPassword);

    void add(HttpServletRequest request, UsersEntity entity);

    UsersEntity deleteUser(HttpServletRequest request, Integer userId);
}
