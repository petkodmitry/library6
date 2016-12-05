package com.petko.services;

import com.petko.entities.BooksEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IBookService {
    void add(HttpServletRequest request, BooksEntity bookEntity);

    BooksEntity deleteBook(HttpServletRequest request, Integer bookId);

    List<BooksEntity> searchBooksByTitleOrAuthor(HttpServletRequest request, String searchTextInBook, String login);
}
