package com.petko.services;

import com.petko.DaoException;
import com.petko.dao.IBookDao;
import com.petko.entities.BooksEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BookService implements IBookService {
    private static Logger log = Logger.getLogger(BookService.class);
    @Autowired
    private IBookDao bookDao;
    @Autowired
    private IUserService userService;

    /**
     * searches Books by Title or Author
     * @param searchTextInBook - text to be searched in Book
     * @return List of Books according conditions
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public List<BooksEntity> searchBooksByTitleOrAuthor(String searchTextInBook, String login) {
        List<BooksEntity> result;
        boolean isUserAdmin = userService.isAdminUser(login);
        try {
            result = bookDao.getBooksByTitleOrAuthorAndStatus(searchTextInBook, null);
            // if User is not Admin, show him only one exemplar
            if (!isUserAdmin) result = showBooksByOneExemplar(result);
            log.info("Search books by (login or title) and status (commit)");
        } catch (DaoException | NullPointerException e) {
            return Collections.emptyList();
        }
        return result;
    }

    private List<BooksEntity> showBooksByOneExemplar(List<BooksEntity> booksEntityList) {
        List<BooksEntity> result = new ArrayList<>();
        Map<Map<String, String>, BooksEntity> tempResult = new HashMap<>();

        for (BooksEntity book : booksEntityList) {
            Map<String, String> conditions = new HashMap<>();
            conditions.put(book.getTitle(), book.getAuthor());
            if (!tempResult.containsKey(conditions)) {
                tempResult.put(conditions, book);
            } else {
                BooksEntity existedBook = tempResult.get(conditions);
                // if exemplar in temp Map is busy and this exemplar is NOT busy - replace
                if (existedBook.getIsBusy() && !book.getIsBusy()) {
                    tempResult.put(conditions, book);
                }
            }
        }
        result.addAll(tempResult.values());

        return result;
    }

    /**
     * removes Book by book Id
     * @param bookId - id of the Book to be deleted
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public BooksEntity deleteBook(Integer bookId) {
        BooksEntity book;
        try {
            book = bookDao.getById(bookId);
            if (book != null) {
                bookDao.delete(book);
                log.info("Delete book (commit)");
            }
        } catch (DaoException e) {
            return null;
        }
        return book;
    }

    /**
     * adds Book to DataBase
     * @param bookEntity to be added to DB
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public void add(BooksEntity bookEntity) {
        try {
            bookDao.save(bookEntity);
            log.info("Save book (commit)");
        } catch (DaoException e) {
        }
    }
}
