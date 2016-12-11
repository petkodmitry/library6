package com.petko.services;

import com.petko.entities.SeminarsEntity;
import org.springframework.ui.ModelMap;

import java.util.List;

public interface ISeminarService {
    void add(SeminarsEntity entity);

    List<SeminarsEntity> getAll();

    List<SeminarsEntity> availableSeminarsForLogin(String login);

    void delete(ModelMap modelMap, int id);

    List<SeminarsEntity> getSeminarsByLogin(String login);

    void subscribeToSeminar(ModelMap modelMap, String login, int seminarId);

    void unSubscribeSeminar(ModelMap modelMap, String login, int seminarId);

    SeminarsEntity getById(int id);
}
