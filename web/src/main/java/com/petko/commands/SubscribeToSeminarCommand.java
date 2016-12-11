package com.petko.commands;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SubscribeToSeminarCommand extends AbstractCommand{
    @Autowired
    private ChooseSeminarsCommand chooseSeminarsCommand;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        Integer seminarId = Integer.parseInt(request.getParameter("seminarId"));
//        seminarService.subscribeToSeminar(request, login, seminarId);

        chooseSeminarsCommand.execute(request, response);
    }
}
