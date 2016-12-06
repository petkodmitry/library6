package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.UsersEntity;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class UserDao extends BaseDao<UsersEntity> implements IUserDao {
    private static Logger log = Logger.getLogger(UserDao.class);

//    @Autowired
//    public UserDao(SessionFactory sessionFactory) {
//        super(sessionFactory);
//    }

    /**
     * gets User by his Login
     * @param login of searched User
     * @return User by his Login
     * @throws DaoException
     */
    /**
    public UsersEntity getByLogin(String login) throws DaoException {
        UsersEntity result;
        try {
            session = util.getSession();
            String hql = "select U from UsersEntity U where U.login=:param";
            Query query = session.createQuery(hql);
//            query.setCacheable(true);
            query.setParameter("param", login);
            result = (UsersEntity) query.uniqueResult();
            log.info("get user by login");
        } catch (HibernateException e) {
            String message = "Error getting user by login in UserDao";
            log.error(message + e);
            throw new DaoException(message);
        }
        return result;
    }
    */
    public UsersEntity getByLogin(String login) throws DaoException {
        UsersEntity result;
        try {
            session = sessionFactory.getCurrentSession();
            String hql = "select U from UsersEntity U where U.login=:param";
            Query query = session.createQuery(hql);
//            query.setCacheable(true);
            query.setParameter("param", login);
            result = (UsersEntity) query.uniqueResult();
            log.info("get user by login");
        } catch (HibernateException e) {
            String message = "Error getting user by login in UserDao. ";
            log.error(message + e);
            throw new DaoException(message);
        }
        return result;
    }

    /**
     * gets Users by their Logins
     * @param logins - Set of searched Users' logins
     * @return Users by their Logins
     * @throws DaoException
     */
    public List<UsersEntity> getAllByCoupleLogins(Set<String> logins) throws DaoException {
        List<UsersEntity> result;
        try {
            session = util.getSession();
            String hql = "SELECT U FROM UsersEntity U WHERE U.login IN :loginsParam";
            Query query = session.createQuery(hql);
            query.setParameterList("loginsParam", logins);
            result = query.list();

            log.info("getAllByCoupleLogins in UserDao");
        } catch (HibernateException e) {
            String message = "Error getAllByCoupleLogins in UserDao";
            log.error(message + e);
            throw new DaoException(message);
        }
        return result;
    }

    /**
     * gets Users by their Block status
     * @param isBlocked - status to be selected
     * @return Users by their Block status
     * @throws DaoException
     */
    public List<UsersEntity> getAllByBlockStatus(Boolean isBlocked) throws DaoException {
        List<UsersEntity> result;
        try {
            session = util.getSession();
            String hql = "SELECT U FROM UsersEntity U WHERE U.isBlocked=:blockParam";
            Query query = session.createQuery(hql);
            query.setParameter("blockParam", isBlocked);
            result = query.list();

            log.info("getAllByBlockStatus in UserDao");
        } catch (HibernateException e) {
            String message = "Error getAllByBlockStatus in UserDao";
            log.error(message + e);
            throw new DaoException(message);
        }
        return result;
    }
}
