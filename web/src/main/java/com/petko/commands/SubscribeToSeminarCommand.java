package com.petko.commands;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SubscribeToSeminarCommand extends AbstractCommand{
    private static SubscribeToSeminarCommand instance;

    private SubscribeToSeminarCommand() {
    }

    public static synchronized SubscribeToSeminarCommand getInstance() {
        if (instance == null) {
            instance = new SubscribeToSeminarCommand();
        }
        return instance;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        Integer seminarId = Integer.parseInt(request.getParameter("seminarId"));
        seminarService.subscribeToSeminar(request, login, seminarId);

        ChooseSeminarsCommand.getInstance().execute(request, response);
    }
}
