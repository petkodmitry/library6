package com.petko.managers;

import com.petko.constants.Constants;
import com.petko.entities.UsersEntity;
import com.petko.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Enumeration;

@Controller
@RequestMapping(value = "/controller")
public class GlobalController {
    private final String errorMessageAttribute = Constants.ERROR_MESSAGE_ATTRIBUTE;
//    private final String forwardPageAttribute = Constants.FORWARD_PAGE_ATTRIBUTE;
    private ModelMap modelMap = new ModelMap();

    @Autowired
    private IUserService userService;
    @Autowired
    private ResourceManager resourceManager;

    @RequestMapping(value = "/login", method = {RequestMethod.POST, RequestMethod.GET})
//    public String login(/*@RequestParam Map<String,String> allRequestParams, */ModelMap modelMap, HttpSession session,
    public String login(ModelMap modelMap, HttpSession session, String login, String password){
//    public String login(ModelMap modelMap, HttpSession session, @Valid UsersEntity usersEntity, BindingResult br){
        this.modelMap = modelMap;
        /*String login = allRequestParams.get("login");
        String password = allRequestParams.get("password");*/

        /*String login = "", password = "";
        if (!br.hasErrors()) {
            login = usersEntity.getLogin();
            password = usersEntity.getPassword();
        }*/

        // TODO перенести в какой-нибудь отдельный метод, а лучше фильтр
        Enumeration<String> attributesNames = session.getAttributeNames();
        while (attributesNames.hasMoreElements()) {
            String attr = attributesNames.nextElement();
            if (!"user".equals(attr)) session.removeAttribute(attr);
        }

        if (session.getAttribute("user") != null) {
            login = (String) session.getAttribute("user");
            return redirectToMainPage(login);
        } else if (!"".equals(login) && userService.isLoginSuccess(login, password)) {
            session.setAttribute("user", login);
            return redirectToMainPage(login);
        } else {
            if ((modelMap.get(errorMessageAttribute)) == null && login != null && !"".equals(login)) {
                modelMap.addAttribute(errorMessageAttribute, "Неверный логин или пароль!");
            }
            return resourceManager.getProperty(Constants.PAGE_INDEX);
        }
    }

    private /*void*/ String redirectToMainPage(String login) {
        String page;
        if (userService.isAdminUser(login)) {
            page = resourceManager.getProperty(Constants.PAGE_MAIN_ADMIN);
        } else {
            page = resourceManager.getProperty(Constants.PAGE_MAIN);
        }
//        setForwardPage(page);
        return page;
    }

    private /*void*/ String setForwardPage(String page) {
//        modelMap.addAttribute(forwardPageAttribute, page);
        return page;
    }
}
