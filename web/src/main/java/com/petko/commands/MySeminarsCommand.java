package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.SeminarsEntity;
import com.petko.services.SeminarService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class MySeminarsCommand extends AbstractCommand{
    private static MySeminarsCommand instance;
    @Autowired
    private ResourceManager resourceManager;

    private MySeminarsCommand() {
    }

    public static synchronized MySeminarsCommand getInstance() {
        if (instance == null) {
            instance = new MySeminarsCommand();
        }
        return instance;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        String page = resourceManager.getProperty(Constants.PAGE_MY_SEMINARS);
        List<SeminarsEntity> mySeminarsList;
        mySeminarsList = seminarService.getSeminarsByLogin(request, login);
        session.setAttribute("mySeminars", mySeminarsList);

        setForwardPage(request, page);
    }
}
