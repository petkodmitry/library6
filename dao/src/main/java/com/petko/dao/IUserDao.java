package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.UsersEntity;

import java.util.List;
import java.util.Set;

public interface IUserDao extends Dao<UsersEntity> {
    UsersEntity getByLogin(String login) throws DaoException;

    List<UsersEntity> getAllByBlockStatus(Boolean isBlocked) throws DaoException;

    List<UsersEntity> getAllByCoupleLogins(Set<String> logins) throws DaoException;
}
