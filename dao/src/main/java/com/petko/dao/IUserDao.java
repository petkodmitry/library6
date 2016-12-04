package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.UsersEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface IUserDao extends Dao<UsersEntity> {
    UsersEntity getByLogin(String login) throws DaoException;

    List<UsersEntity> getAllByBlockStatus(Boolean isBlocked) throws DaoException;
}
