package com.petko.services;

import com.petko.entities.SeminarsEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ISeminarService {
    void add(HttpServletRequest request, SeminarsEntity entity);

    List<SeminarsEntity> getAll(HttpServletRequest request);

    List<SeminarsEntity> availableSeminarsForLogin(HttpServletRequest request, String login);

    void delete(HttpServletRequest request, int id);

    List<SeminarsEntity> getSeminarsByLogin(HttpServletRequest request, String login);

    void subscribeToSeminar(HttpServletRequest request, String login, int seminarId);

    void unSubscribeSeminar(HttpServletRequest request, String login, int seminarId);

    SeminarsEntity getById(HttpServletRequest request, int id);
}
