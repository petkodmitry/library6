package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.SeminarsEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ISeminarDao extends Dao<SeminarsEntity> {
    List<SeminarsEntity> getSeminarsByLogin(String login) throws DaoException;

    List<SeminarsEntity> getAll() throws DaoException;
}
