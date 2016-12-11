package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.UsersEntity;
import com.petko.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RegisterCommand extends AbstractCommand {
    @Autowired
    private ResourceManager resourceManager;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        if (login == null || userService.isAdminUser(/*request,*/ login)) {

            String page = resourceManager.getProperty(Constants.PAGE_REGISTRATION);
            UsersEntity regData;
            /**
             * creating attribute of the session: UsersEntity regData
             */
            if (session.getAttribute("regData") == null) {
                regData = new UsersEntity();
                session.setAttribute("regData", regData);
            }
            /**
             * reading data from session attribute regData
             */
            else {
                regData = (UsersEntity) session.getAttribute("regData");
                regData = userService.setAllDataOfEntity(regData, request.getParameter("newName"), request.getParameter("newLastName"),
                        request.getParameter("newLogin"), request.getParameter("newPassword"), false, false);
                String repeatPassword = request.getParameter("repeatPassword");
                /**
                 * if 'login' is entered
                 */
                if (regData.getLogin() != null && !"".equals(regData.getLogin())) {
                    /**
                     * check if asked login exists in database
                     */
                    if (userService.isLoginExists(regData.getLogin())) {
                        request.setAttribute("unavailableMessage", "логин НЕдоступен!");
                    } else {
                        request.setAttribute("unavailableMessage", "логин доступен");
                        /**
                         * if all data is entered
                         */
                        if (userService.isAllRegisterDataEntered(regData, repeatPassword)) {
                            if (userService.isAllPasswordRulesFollowed(regData.getPassword(), repeatPassword)) {
                                userService.add(regData);
                                session.removeAttribute("regData");
                                page = resourceManager.getProperty(Constants.PAGE_REGISTRATION_OK);
                            } else {
                                setErrorMessage(request, "Пароль должен содержать 8 символов");
                            }
                        }
                    }
                }
            }
            setForwardPage(request, page);
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
