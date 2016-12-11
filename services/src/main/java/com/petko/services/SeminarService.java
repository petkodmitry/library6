package com.petko.services;

import com.petko.DaoException;
import com.petko.constants.Constants;
import com.petko.dao.ISeminarDao;
import com.petko.dao.IUserDao;
import com.petko.entities.SeminarsEntity;
import com.petko.entities.UsersEntity;
import com.petko.utils.HibernateUtilLibrary;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class SeminarService implements ISeminarService {
    private static Logger log = Logger.getLogger(SeminarService.class);
    @Autowired
    private ISeminarDao seminarDao;
    @Autowired
    private IUserDao userDao;

    /**
     * gives List of Seminars for User
     * @param login - login of the User
     * @return List of Seminars for User
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public List<SeminarsEntity> getSeminarsByLogin(String login) {
        List<SeminarsEntity> result = new ArrayList<>();
        try {
            result = seminarDao.getSeminarsByLogin(login);
            log.info("Get seminars by login (commit)");
        } catch (DaoException e) {
//            ExceptionsHandler.processException(request, e);
        }
        return result;
    }

    /**
     * Subscribe User to Seminar
     * @param modelMap - current http request map
     * @param login of the User to subscribe
     * @param seminarId to be subscribed
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public void subscribeToSeminar(ModelMap modelMap, String login, int seminarId) {
        try {
            UsersEntity user = userDao.getByLogin(login);
            SeminarsEntity seminar = seminarDao.getById(seminarId);
            if (user != null && seminar != null) {
                user.getSeminars().add(seminar);
                modelMap.addAttribute(Constants.INFO_MESSAGE_ATTRIBUTE, "Вы успешно записались на семинар '" + seminar.getSubject() + "'.");
            }
            else modelMap.addAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE, "Не удалось записаться на выбранный семинар");
            log.info("Get user by login (commit)");
            log.info("Get seminar by id (commit)");
        } catch (DaoException e) {
//            ExceptionsHandler.processException(request, e);
        }
    }

    /**
     * UnSubscribe User from Seminar
     * @param modelMap - current http request map
     * @param login of the User to unSubscribe
     * @param seminarId to be unSubscribed
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public void unSubscribeSeminar(ModelMap modelMap, String login, int seminarId) {
        try {
            UsersEntity user = userDao.getByLogin(login);
            SeminarsEntity seminar = seminarDao.getById(seminarId);
            if (user != null && seminar != null) {
                user.getSeminars().remove(seminar);
                modelMap.addAttribute(Constants.INFO_MESSAGE_ATTRIBUTE, "Вы успешно отписались от семинара '" + seminar.getSubject() + "'.");
            }
            else modelMap.addAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE, "Не удалось отписаться от выбранного семинара.");
            log.info("Get user by login (commit)");
            log.info("Get seminar by id (commit)");
        } catch (DaoException e) {
//            ExceptionsHandler.processException(request, e);
        }
    }

    /**
     * gives List of Seminars for specific User
     * @param login to be searched in seminars
     * @return List of Seminars for specific User
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public List<SeminarsEntity> availableSeminarsForLogin(String login) {
        List<SeminarsEntity> result = new ArrayList<>();
        try {
            result = seminarDao.getAll();
            result.removeAll(seminarDao.getSeminarsByLogin(login));
            log.info("Get all seminars >= today (commit)");
            log.info("Get seminars by login (commit)");
        } catch (DaoException e) {
//            ExceptionsHandler.processException(request, e);
        }
        return result;
    }

    // gives all future Seminars
    @Override
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public List<SeminarsEntity> getAll() {
        List<SeminarsEntity> result = new ArrayList<>();
        try {
            result = seminarDao.getAll();
            log.info("Get all seminars >= today (commit)");
        } catch (DaoException e) {
//            ExceptionsHandler.processException(request, e);
        }
        return result;
    }

    /**
     * adds SeminarsEntity to DataBase
     * @param entity to be added
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public void add(SeminarsEntity entity) {
        try {
            seminarDao.save(entity);
            log.info("Add seminar to DB (commit)");
        } catch (DaoException e) {
//            ExceptionsHandler.processException(request, e);
        }
    }

    /**
     * removes SeminarsEntity from DataBase
     * @param modelMap - current http request map
     * @param id of the Seminar to be deleted
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public void delete(ModelMap modelMap, int id) {
        try {
            SeminarsEntity entity = seminarDao.getById(id);
            if (entity != null) {
                entity.getUsers().clear();
                seminarDao.delete(entity);
            } else {
                modelMap.addAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE, "Не удалось удалить выбранный семинар.");
            }
            log.info("Get seminar by id (commit)");
            log.info("Delete seminar from DB (commit)");
        } catch (DaoException e) {
//            ExceptionsHandler.processException(request, e);
        }
    }

    /**
     * gives Seminar by its ID
     * @param id to be searched
     * @return Seminar by its ID
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public SeminarsEntity getById(int id) {
        SeminarsEntity result = null;
        try {
            result = seminarDao.getById(id);
            log.info("Get seminar by id (commit)");
        } catch (DaoException e) {
//            ExceptionsHandler.processException(request, e);
        }
        return result;
    }
}
