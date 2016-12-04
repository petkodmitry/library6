package com.petko.services;

import javax.servlet.http.HttpServletRequest;

public interface IUserService {
    boolean isLoginSuccess(HttpServletRequest request, String login, String password);

    boolean isAdminUser(HttpServletRequest request, String login);
}
