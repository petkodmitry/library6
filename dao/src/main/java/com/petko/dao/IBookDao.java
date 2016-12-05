package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.BooksEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

public interface IBookDao extends Dao<BooksEntity> {
    List<BooksEntity> getBooksByTitleOrAuthorAndStatus(String searchTextInBook, Boolean status) throws DaoException;

    List<BooksEntity> getAllByCoupleIds(Set<Integer> ids) throws DaoException;

    List<BooksEntity> getBooksByTitleAndAuthorAndStatus(String title, String author, Boolean isBusy) throws DaoException;
}
