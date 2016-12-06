package com.petko.managers;

import com.petko.commands.AbstractCommand;
import com.petko.services.IUserService;
import org.aspectj.bridge.ICommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.*;

@Controller
@RequestMapping/*("/controller")*/
public class GlobalController {

    @Autowired
    private IUserService userService;
    @Autowired
    private AbstractCommand loginCommand;

    @RequestMapping("controller")
//    @RequestMapping
    /*protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ModelAndView view;
        String command = httpServletRequest.getParameter("cmd");
        String login = httpServletRequest.getParameter("login");
        String password = httpServletRequest.getParameter("password");
        if (userService.isAdminUser(httpServletRequest, login)) {
            view = new ModelAndView("mainadmin");
        } else {
            view = new ModelAndView("main");
        }
        view.addObject("user", login);
        // TODO - проверка логина/пароля
        return view;
    }*/
    protected ModelAndView handleRequestInternal(ModelAndView view, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
//        ModelAndView view;
        loginCommand.execute(httpServletRequest, httpServletResponse);
        return view;
    }

    /*@RequestMapping("controller?cmd=showUsers")
//    @RequestMapping
    protected ModelAndView handleRequestInternal2(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ModelAndView view;
        String command = httpServletRequest.getParameter("cmd");
        String login = httpServletRequest.getParameter("login");
        String password = httpServletRequest.getParameter("password");
        if (userService.isAdminUser(httpServletRequest, login)) {
            view = new ModelAndView("mainadmin");
        } else {
            view = new ModelAndView("main");
        }
        view.addObject("user", login);
        return view;
    }*/
}
