package com.petko.controllers;

import com.petko.managers.ResourceManager;
import com.petko.constants.Constants;
import com.petko.entities.BooksEntity;
import com.petko.entities.OrderStatus;
import com.petko.entities.SeminarsEntity;
import com.petko.entities.UsersEntity;
import com.petko.services.IBookService;
import com.petko.services.IOrderService;
import com.petko.services.ISeminarService;
import com.petko.services.IUserService;
import com.petko.vo.FullOrdersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = {"/controller", "/"})
public class GlobalController {
    private final String errorMessageAttribute = Constants.ERROR_MESSAGE_ATTRIBUTE;
    private final String infoMessageAttribute = Constants.INFO_MESSAGE_ATTRIBUTE;

    @Autowired
    private IUserService userService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IBookService bookService;
    @Autowired
    private ISeminarService seminarService;

    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private CookieLocaleResolver localeResolver;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login0(ModelMap modelMap, HttpSession session) {
        Enumeration<String> attributesNames = session.getAttributeNames();
        while (attributesNames.hasMoreElements()) {
            String attr = attributesNames.nextElement();
            if (!"user".equals(attr)) session.removeAttribute(attr);
        }
        String login = (String) session.getAttribute("user");
        if (login != null) {
            return redirectToMainPage(login);
        }
        else return resourceManager.getProperty(Constants.PAGE_INDEX);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(ModelMap modelMap, HttpServletRequest request, HttpSession session, String login, String password) {
        if (!"".equals(login) && userService.isLoginSuccess(login, password)) {
            session.setAttribute("user", login);
            return redirectToMainPage(login);
        } else {
            if ((modelMap.get(errorMessageAttribute)) == null) {
                String message = convertStringToUtf8Charset(request, "password.wrong");
                modelMap.addAttribute(errorMessageAttribute, message);
            }
            return resourceManager.getProperty(Constants.PAGE_INDEX);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        String login = (String) session.getAttribute("user");
        userService.logOut(session, login);
        return redirectToLoginPage();
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register0(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        if (login == null || userService.isAdminUser(login)) {
            return resourceManager.getProperty(Constants.PAGE_REGISTRATION);
        } else {
            if ((modelMap.get(errorMessageAttribute)) == null) {
                modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
            }
            return redirectToMainPage(login);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(ModelMap modelMap, HttpSession session, @Valid UsersEntity regData,
                           @RequestParam(value = "repeatPassword", required = false) String repeatPassword
    ) {
        session.setAttribute("regData", regData);
                /**
                 * if 'login' is entered
                 */
                if (!"".equals(regData.getLogin())) {
                    /**
                     * check if asked login exists in database
                     */
                    if (userService.isLoginExists(regData.getLogin())) {
                        modelMap.addAttribute("unavailableMessage", "логин НЕдоступен!");
                    } else {
                        modelMap.addAttribute("unavailableMessage", "логин доступен");
                        /**
                         * if all data is entered
                         */
                        if (userService.isAllRegisterDataEntered(regData, repeatPassword)) {
                            if (userService.isAllPasswordRulesFollowed(regData.getPassword(), repeatPassword)) {
                                userService.add(regData);
                                session.removeAttribute("regData");
                                return resourceManager.getProperty(Constants.PAGE_REGISTRATION_OK);
                            } else {
                                modelMap.addAttribute(errorMessageAttribute, "Пароль должен содержать не менее 8 символов.");
                            }
                        }
                    }
                }
        return resourceManager.getProperty(Constants.PAGE_REGISTRATION);
    }

    @RequestMapping(value = "/myBooks", method = RequestMethod.GET)
    public String myBooks(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        String page = resourceManager.getProperty(Constants.PAGE_MY_BOOKS);
        List<FullOrdersList> myBooksList;
        myBooksList = orderService.getOrdersByLoginAndStatus(login, OrderStatus.ON_HAND);
        modelMap.addAttribute("myBooksList", myBooksList);
        return page;
    }

    @RequestMapping(value = "/prolongOrder", method = RequestMethod.GET)
    public String prolongOrder(ModelMap modelMap, HttpSession session, String orderId) {
        String login = (String) session.getAttribute("user");
        try {
            Integer oId = Integer.parseInt(orderId);
            orderService.prolongOrder(modelMap, login, oId);
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать ID заказа.");
        }
        return myBooks(modelMap, session);
    }

    @RequestMapping(value = "/searchBook", method = RequestMethod.GET)
    public String searchBook0(ModelMap modelMap) {
        return resourceManager.getProperty(Constants.PAGE_SEARCH_BOOK_FOR_USER);
    }

    @RequestMapping(value = "/searchBook", method = RequestMethod.POST)
    public String searchBook(ModelMap modelMap, HttpSession session, String searchTextInBook) {
        String login = (String) session.getAttribute("user");
        List<BooksEntity> searchBookForUser;
        if (!"".equals(searchTextInBook)) {
            searchBookForUser = bookService.searchBooksByTitleOrAuthor(searchTextInBook, login);
            if (searchBookForUser.isEmpty()) modelMap.addAttribute(infoMessageAttribute, "По Вашему запросу ничего не найдено.");
            session.setAttribute("searchBookForUser", searchBookForUser);
        }
        return resourceManager.getProperty(Constants.PAGE_SEARCH_BOOK_FOR_USER);
    }

    // mapping for pathVariables which equals "orderTo*"
    @RequestMapping(value = "/{path:orderTo+[A-Za-z-]+}", method = RequestMethod.GET)
    public String orderToHome(ModelMap modelMap, HttpSession session, String bookId,
                              @PathVariable String path) {
        String login = (String) session.getAttribute("user");
        try {
            int bId = Integer.parseInt(bookId);
            if ("orderToHome".equals(path)) orderService.orderToHomeOrToRoom(modelMap, login, bId, true);
            else if ("orderToReadingRoom".equals(path)) orderService.orderToHomeOrToRoom(modelMap, login, bId, false);
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать ID книги.");
        }
        return resourceManager.getProperty(Constants.PAGE_SEARCH_BOOK_FOR_USER);
    }

    @RequestMapping(value = "/myOrders", method = RequestMethod.GET)
    public String myOrders(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        List<FullOrdersList> myOrdersList;
        myOrdersList = orderService.getOrdersByLoginAndStatus(login, OrderStatus.ORDERED);
        modelMap.addAttribute("myOrdersList", myOrdersList);
        return resourceManager.getProperty(Constants.PAGE_MY_ORDERS);
    }

    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.GET)
    public String cancelUserOrder(ModelMap modelMap, HttpSession session, String orderId) {
        String login = (String) session.getAttribute("user");
        try {
            int oId = Integer.parseInt(orderId);
            orderService.closeOrder(login, oId);
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать ID заказа.");
        }
        return myOrders(modelMap, session);
    }

    @RequestMapping(value = "/mySeminars", method = RequestMethod.GET)
    public String mySeminars(HttpSession session) {
        String login = (String) session.getAttribute("user");
        List<SeminarsEntity> mySeminarsList;
        mySeminarsList = seminarService.getSeminarsByLogin(login);
        session.setAttribute("mySeminars", mySeminarsList);
        return resourceManager.getProperty(Constants.PAGE_MY_SEMINARS);
    }

    @RequestMapping(value = "/unSubscribeSeminar", method = RequestMethod.GET)
    public String unSubscribeSeminar(ModelMap modelMap, HttpSession session, String seminarId) {
        String login = (String) session.getAttribute("user");
        try {
            Integer sId = Integer.parseInt(seminarId);
            seminarService.unSubscribeSeminar(modelMap, login, sId);
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать ID семинара.");
        }
        return mySeminars(session);
    }

    @RequestMapping(value = "/chooseSeminars", method = RequestMethod.GET)
    public String chooseSeminars(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        List<SeminarsEntity> availableSeminarsList;
        availableSeminarsList = seminarService.availableSeminarsForLogin(login);
        session.setAttribute("availableSeminars", availableSeminarsList);
        return resourceManager.getProperty(Constants.PAGE_CHOOSE_SEMINARS);
    }

    @RequestMapping(value = "/subscribeToSeminar", method = RequestMethod.GET)
    public String subscribeToSeminar(ModelMap modelMap, HttpSession session, String seminarId) {
        String login = (String) session.getAttribute("user");
        try {
            Integer sId = Integer.parseInt(seminarId);
            seminarService.subscribeToSeminar(modelMap, login, sId);
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать ID семинара.");
        }
        return chooseSeminars(modelMap, session);
    }

    @RequestMapping(value = "/showUsers", method = RequestMethod.GET)
    public String showUsers0(ModelMap modelMap, HttpSession session,
                             @RequestParam(value = "page", required = false) String page,
                             @RequestParam(value = "sortBy", required = false) String sortBy,
                             @RequestParam(value = "orderType", required = false) String orderType,
                             @RequestParam(value = "filterRemove", required = false) String filterRemove) {
        String login = (String) session.getAttribute("user");
        if (!userService.isAdminUser(login)) {
            if ((modelMap.get(errorMessageAttribute)) == null) {
                modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
            }
            return redirectToMainPage(login);
        }
        HashMap<String, String> filters = (HashMap<String, String>) session.getAttribute("filters");
        filters = filters == null ? new HashMap<>() : filters;
        if (page != null) modelMap.addAttribute("page", page);
        if (sortBy != null) session.setAttribute("sortBy", sortBy);
        if (orderType != null) session.setAttribute("orderType", orderType);
        if (filterRemove != null) modelMap.addAttribute("filterRemove", filterRemove);
//        filters = null;               // для тестов по ловле Exception @ExceptionHandler'ом
        if (filterRemove != null) filters.remove(filterRemove);
        session.setAttribute("filters", filters);
        modelMap.addAttribute("userSet", userService.getAll(modelMap, session));
        return resourceManager.getProperty(Constants.PAGE_SHOW_USERS);
    }

    @RequestMapping(value = "/showUsers", method = RequestMethod.POST)
    public String showUsers(ModelMap modelMap, HttpSession session, String perPage,
                            @RequestParam(value = "page", required = false) String page,
                            @RequestParam(value = "filterSet", required = false) String filterSet,
                            @RequestParam(value = "filterText", required = false) String filterText) {
            HashMap<String, String> filters = (HashMap<String, String>) session.getAttribute("filters");
            if (filterSet != null && filterText != null && !filterText.equals("")) {
                filters.put(filterSet, filterText);
            }
            if (perPage != null) modelMap.addAttribute("perPage", perPage);
            if (page != null) modelMap.addAttribute("page", page);
            modelMap.addAttribute("userSet", userService.getAll(modelMap, session));
            return resourceManager.getProperty(Constants.PAGE_SHOW_USERS);
    }

    @RequestMapping(value = "/openedOrders", method = RequestMethod.GET)
    public String openedOrders(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            List<FullOrdersList> openedOrdersList = orderService.getOrdersByLoginAndStatus(null, OrderStatus.ON_HAND);
            session.setAttribute("openedOrdersList", openedOrdersList);
            return resourceManager.getProperty(Constants.PAGE_OPENED_ORDERS);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/waitingOrders", method = RequestMethod.GET)
    public String waitingOrders(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            List<FullOrdersList> waitingOrdersList = orderService.getOrdersByLoginAndStatus(null, OrderStatus.ORDERED);
            session.setAttribute("waitingOrdersList", waitingOrdersList);
            return resourceManager.getProperty(Constants.PAGE_WAITING_ORDERS);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/provideBook", method = RequestMethod.GET)
    public String provideBook(ModelMap modelMap, HttpSession session, String orderId) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            try {
                Integer oId = Integer.parseInt(orderId);
                orderService.provideBook(modelMap, oId);
            } catch (NumberFormatException e) {
                modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать ID заказа.");
            }
            return waitingOrders(modelMap, session);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/closeOrder", method = RequestMethod.GET)
    public String closeOrder(ModelMap modelMap, HttpSession session, String orderId) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            try {
                int oId = Integer.parseInt(orderId);
                String status = orderService.getById(oId).getStatus();
                orderService.closeOrder(null, oId);
                if (OrderStatus.ORDERED.toString().equals(status)) {
                    return waitingOrders(modelMap, session);
                } else if (OrderStatus.ON_HAND.toString().equals(status)) {
                    return openedOrders(modelMap, session);
                }
            } catch (NumberFormatException e) {
                modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать ID заказа.");
            }
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/expiredOrders", method = RequestMethod.GET)
    public String expiredOrders(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            List<FullOrdersList> expiredOrdersList = orderService.getExpiredOrders();
            session.setAttribute("expiredOrdersList", expiredOrdersList);
            return resourceManager.getProperty(Constants.PAGE_EXPIRED_ORDERS);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/blockUser", method = RequestMethod.GET)
    public String blockUser(ModelMap modelMap, HttpSession session,
                            @RequestParam(value = "login", required = false) String userLogin) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            userService.setBlockUser(userLogin, true);
            return expiredOrders(modelMap, session);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/blacklist", method = RequestMethod.GET)
    public String blacklist(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            List<UsersEntity> blackList = userService.getUsersByBlock(true);
            session.setAttribute("blackList", blackList);
            return resourceManager.getProperty(Constants.PAGE_BLACK_LIST);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/unblockUser", method = RequestMethod.GET)
    public String unblockUser(ModelMap modelMap, HttpSession session,
                              @RequestParam(value = "login", required = false) String userLogin) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            userService.setBlockUser(userLogin, false);
            return blacklist(modelMap, session);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/searchBookAdmin", method = RequestMethod.GET)
    public String searchBookAdmin0(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        if (!userService.isAdminUser(login)) {
            if ((modelMap.get(errorMessageAttribute)) == null) {
                modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
            }
            return redirectToMainPage(login);
        }
        return resourceManager.getProperty(Constants.PAGE_SEARCH_BOOK_ADMIN);
    }

    @RequestMapping(value = "/searchBookAdmin", method = RequestMethod.POST)
    public String searchBookAdmin(ModelMap modelMap, HttpSession session, String searchTextInBook) {
        String login = (String) session.getAttribute("user");
            if (!"".equals(searchTextInBook)) {
                List<BooksEntity> searchBookAdmin = bookService.searchBooksByTitleOrAuthor(searchTextInBook, login);
                if (searchBookAdmin.isEmpty()) modelMap.addAttribute(infoMessageAttribute, "По Вашему запросу ничего не найдено.");
                session.setAttribute("searchBookAdmin", searchBookAdmin);
            }
            return resourceManager.getProperty(Constants.PAGE_SEARCH_BOOK_ADMIN);
    }

    @RequestMapping(value = "/addBook", method = RequestMethod.GET)
    public String addBook0(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        if (!userService.isAdminUser(login)) {
            if ((modelMap.get(errorMessageAttribute)) == null) {
                modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
            }
            return redirectToMainPage(login);
        }
        session.setAttribute("regData", new BooksEntity());
        return resourceManager.getProperty(Constants.PAGE_ADD_BOOK);
    }

    @RequestMapping(value = "/addBook", method = RequestMethod.POST)
    public String addBook(ModelMap modelMap, HttpSession session, String newTitle, String newAuthor) {
            BooksEntity regData = (BooksEntity) session.getAttribute("regData");
            if (regData == null) {
                regData = new BooksEntity();
                session.setAttribute("regData", regData);
            } else {
                regData.setTitle(newTitle);
                regData.setAuthor(newAuthor);
                if (!"".equals(newTitle) && !"".equals(newAuthor)) {
                    regData.setIsBusy(false);
                    bookService.add(regData);
                    modelMap.addAttribute(infoMessageAttribute, "Книга '" + newTitle + "' автора '" + newAuthor + "' добавлена в базу библиотеки.");
                    regData.setTitle("");
                    regData.setAuthor("");
                } else modelMap.addAttribute(errorMessageAttribute, "Поля данных книги не должны быть пустыми.");
            }
            return resourceManager.getProperty(Constants.PAGE_ADD_BOOK);
    }

    @RequestMapping(value = "/deleteBook", method = RequestMethod.GET)
    public String deleteBook(ModelMap modelMap, HttpSession session, String bookId) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            Integer bId = Integer.parseInt(bookId);
            BooksEntity book = bookService.deleteBook(bId);
            if (book != null) modelMap.addAttribute(infoMessageAttribute, "Книга с ID " + bId + " успешно удалена.");
            List<BooksEntity> searchBookAdmin = (ArrayList<BooksEntity>) session.getAttribute("searchBookAdmin");
            if (searchBookAdmin != null) searchBookAdmin.remove(book);
            return searchBookAdmin(modelMap, session, null);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/adminSeminars", method = RequestMethod.GET)
    public String adminSeminars(ModelMap modelMap, HttpSession session) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            List<SeminarsEntity> allSeminars = seminarService.getAll();
            session.setAttribute("allSeminars", allSeminars);
            return resourceManager.getProperty(Constants.PAGE_ADMIN_SEMINARS);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }


    @RequestMapping(value = "/addSeminar", method = RequestMethod.GET)
    public String addSeminar0(ModelMap modelMap) {
        return resourceManager.getProperty(Constants.PAGE_ADD_SEMINAR);
    }

    @RequestMapping(value = "/addSeminar", method = RequestMethod.POST)
    public String addSeminar(ModelMap modelMap, HttpSession session, String newSubject, String newDate) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            Date date = null;
            if (!"".equals(newDate)) {
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(newDate);
                } catch (ParseException e) {/*NOP*/}
            }
            Date today1 = new Date();
            Date today = new Date(today1.getYear(), today1.getMonth(), today1.getDate());
            if (!"".equals(newSubject) && date != null && (today.before(date) || today.equals(date))) {
                SeminarsEntity entity = new SeminarsEntity();
                entity.setSubject(newSubject);
                entity.setSeminarDate(date);
                seminarService.add(entity);
                modelMap.addAttribute(infoMessageAttribute, "Новый семинар по теме '" + newSubject + "' успешно добавлен.");
            } else {
                modelMap.addAttribute("newSubject", newSubject);
                modelMap.addAttribute("newDate", newDate);
                modelMap.addAttribute(errorMessageAttribute, "Поля данных семинара не должны быть пустыми и дата должна быть >= сегодня.");
            }
            return resourceManager.getProperty(Constants.PAGE_ADD_SEMINAR);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/usersOfSeminar", method = RequestMethod.GET)
    public String usersOfSeminar(ModelMap modelMap, HttpSession session, String seminarId) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            Integer sId = Integer.parseInt(seminarId);
            SeminarsEntity seminarEntity = seminarService.getById(sId);
            session.setAttribute("seminarEntity", seminarEntity);
            return resourceManager.getProperty(Constants.PAGE_ADMIN_SEMINARS);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/deleteSeminar", method = RequestMethod.GET)
    public String deleteSeminar(ModelMap modelMap, HttpSession session, String seminarId) {
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            Integer sId = Integer.parseInt(seminarId);
            seminarService.delete(modelMap, sId);
            modelMap.addAttribute(infoMessageAttribute, "Семинар с ID " + seminarId + " успешно удалён.");
            return adminSeminars(modelMap, session);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    // mapping for Unknown commands
    @RequestMapping(value = "/{path:[0-9А-Яа-яA-Za-z-+_*]+}", method = RequestMethod.GET)
    public String unknown(ModelMap modelMap,
                          @PathVariable String path) {
        modelMap.addAttribute(errorMessageAttribute, "Команда не распознана или не задана!");
        return resourceManager.getProperty(Constants.PAGE_ERROR);
    }

    private String redirectToMainPage(String login) {
        String page;
        if (userService.isAdminUser(login)) {
            page = resourceManager.getProperty(Constants.PAGE_MAIN_ADMIN);
        } else {
            page = resourceManager.getProperty(Constants.PAGE_MAIN);
        }
        return page;
    }

    private String redirectToLoginPage() {
        return resourceManager.getProperty(Constants.PAGE_INDEX);
    }

    private String convertStringToUtf8Charset(HttpServletRequest request, String bundleKey) {
        Locale locale = localeResolver.resolveLocale(request);
        String message = resourceManager.getResourceBundleLocale(locale).getString(bundleKey);
        return new String(message.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("utf-8"));
    }
}
