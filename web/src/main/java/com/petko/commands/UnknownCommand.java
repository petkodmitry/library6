package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UnknownCommand extends AbstractCommand{
    @Autowired
    private ResourceManager resourceManager;

    public void execute(HttpServletRequest request, HttpServletResponse response) {
        setErrorMessage(request, "Команда не распознана или не задана!");
        String page = resourceManager.getProperty(Constants.PAGE_ERROR);
        setForwardPage(request, page);
    }
}
