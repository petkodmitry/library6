package com.petko.commands;

import com.petko.services.SeminarService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UnSubscribeSeminarCommand extends AbstractCommand{
    @Autowired
    private MySeminarsCommand mySeminarsCommand;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        Integer seminarId = Integer.parseInt(request.getParameter("seminarId"));
//        seminarService.unSubscribeSeminar(request, login, seminarId);
        mySeminarsCommand.execute(request, response);
    }
}
