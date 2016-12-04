package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.BooksEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface IBookDao extends Dao<BooksEntity> {
    List<BooksEntity> getBooksByTitleOrAuthorAndStatus(String searchTextInBook, Boolean status) throws DaoException;
}
