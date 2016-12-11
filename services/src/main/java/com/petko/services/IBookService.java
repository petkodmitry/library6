package com.petko.services;

import com.petko.entities.BooksEntity;

import java.util.List;

public interface IBookService {
    void add(BooksEntity bookEntity);

    BooksEntity deleteBook(Integer bookId);

    List<BooksEntity> searchBooksByTitleOrAuthor(String searchTextInBook, String login);
}
