package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.SeminarsEntity;
import com.petko.services.SeminarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ChooseSeminarsCommand extends AbstractCommand{
    @Autowired
    private ResourceManager resourceManager;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        String page = resourceManager.getProperty(Constants.PAGE_CHOOSE_SEMINARS);
        List<SeminarsEntity> availableSeminarsList;
        availableSeminarsList = seminarService.availableSeminarsForLogin(login);
        session.setAttribute("availableSeminars", availableSeminarsList);

        setForwardPage(request, page);
    }
}
