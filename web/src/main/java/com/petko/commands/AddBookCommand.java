package com.petko.commands;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.BooksEntity;
import com.petko.services.BookService;
import com.petko.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AddBookCommand extends AbstractCommand {
    @Autowired
    private ResourceManager resourceManager;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(/*request,*/ login)) {
            BooksEntity regData = (BooksEntity) session.getAttribute("regData");
            String page = resourceManager.getProperty(Constants.PAGE_ADD_BOOK);
            if (regData == null) {
                regData = new BooksEntity();
                session.setAttribute("regData", regData);
            } else {
                String title = request.getParameter("newTitle");
                String author = request.getParameter("newAuthor");
                regData.setTitle(title);
                regData.setAuthor(author);
                if (title != null && author != null && !"".equals(title) && !"".equals(author)) {
                    regData.setIsBusy(false);
                    bookService.add(regData);
                    request.setAttribute("info", "Книга добавлена в базу библиотеки");
                    regData.setTitle("");
                    regData.setAuthor("");
                } else if (title != null && author != null) {
                    setErrorMessage(request, "Поля данных книги не должны быть пустыми");
                }
            }

            setForwardPage(request, page);
        // если не админ, сообщаем о невозможности выполнения команды
        } else if ((request.getAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE)) == null) {
            setErrorMessage(request, "У Вас нет прав для выполнения данной команды");
            redirectToMainPage(request, login);
        }
    }
}
