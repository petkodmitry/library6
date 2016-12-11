package com.petko.managers;

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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/controller")
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

    @RequestMapping(value = "/login", method = {RequestMethod.POST, RequestMethod.GET})
    public String login(ModelMap modelMap, HttpSession session, String login, String password){
//    public String login(ModelMap modelMap, HttpSession session, @Valid UsersEntity usersEntity, BindingResult br){
        /*String login = allRequestParams.get("login");
        String password = allRequestParams.get("password");*/

        /*String login = "", password = "";
        if (!br.hasErrors()) {
            login = usersEntity.getLogin();
            password = usersEntity.getPassword();
        }*/

        // TODO перенести в какой-нибудь отдельный метод, а лучше фильтр
        Enumeration<String> attributesNames = session.getAttributeNames();
        while (attributesNames.hasMoreElements()) {
            String attr = attributesNames.nextElement();
            if (!"user".equals(attr)) session.removeAttribute(attr);
        }

        if (session.getAttribute("user") != null) {
            login = (String) session.getAttribute("user");
            return redirectToMainPage(login);
        } else if (!"".equals(login) && userService.isLoginSuccess(login, password)) {
            session.setAttribute("user", login);
            return redirectToMainPage(login);
        } else {
            if ((modelMap.get(errorMessageAttribute)) == null && login != null && !"".equals(login)) {
                modelMap.addAttribute(errorMessageAttribute, "Неверный логин или пароль!");
            }
            return resourceManager.getProperty(Constants.PAGE_INDEX);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session){
        String login = (String) session.getAttribute("user");
        userService.logOut(session, login);
        return redirectToLoginPage();
    }

    @RequestMapping(value = "/myBooks", method = RequestMethod.GET)
    public String myBooks(ModelMap modelMap, HttpSession session){
        String login = (String) session.getAttribute("user");
        String page = resourceManager.getProperty(Constants.PAGE_MY_BOOKS);
        List<FullOrdersList> myBooksList;
        myBooksList = orderService.getOrdersByLoginAndStatus(login, OrderStatus.ON_HAND);
//        session.setAttribute("myBooksList", myBooksList);
        modelMap.addAttribute("myBooksList", myBooksList);
        return page;
    }

    @RequestMapping(value = "/prolongOrder", method = RequestMethod.GET)
    public String prolongOrder(ModelMap modelMap, HttpSession session, String orderId){
        String login = (String) session.getAttribute("user");
        try {
            Integer oId = Integer.parseInt(orderId);
            orderService.prolongOrder(modelMap, login, oId);
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать ID заказа.");
        }
        return myBooks(modelMap, session);
    }

    @RequestMapping(value = "/searchBook", method = {RequestMethod.GET, RequestMethod.POST})
    public String searchBook(ModelMap modelMap, HttpSession session, String searchTextInBook){
        String login = (String) session.getAttribute("user");
        List<BooksEntity> searchBookForUser;
        // if there is searchTextInBook parameter in request
        if (searchTextInBook != null && !"".equals(searchTextInBook)) {
            searchBookForUser = bookService.searchBooksByTitleOrAuthor(searchTextInBook, login);
            session.setAttribute("searchBookForUser", searchBookForUser);
        }
        return resourceManager.getProperty(Constants.PAGE_SEARCH_BOOK_FOR_USER);
    }

    // mapping for pathVariables which equals "orderTo*"
    @RequestMapping(value = "/{path:orderTo+[A-Za-z-]+}", method = RequestMethod.GET)
    public String orderToHome(ModelMap modelMap, HttpSession session, String bookId,
                              @PathVariable String path){
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
    public String myOrders(ModelMap modelMap, HttpSession session){
        String login = (String) session.getAttribute("user");
        List<FullOrdersList> myOrdersList;
        myOrdersList = orderService.getOrdersByLoginAndStatus(login, OrderStatus.ORDERED);
        modelMap.addAttribute("myOrdersList", myOrdersList);
        return resourceManager.getProperty(Constants.PAGE_MY_ORDERS);
    }

    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.GET)
    public String cancelUserOrder(ModelMap modelMap, HttpSession session, String orderId){
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
    public String mySeminars(HttpSession session){
        String login = (String) session.getAttribute("user");
        List<SeminarsEntity> mySeminarsList;
        mySeminarsList = seminarService.getSeminarsByLogin(login);
        session.setAttribute("mySeminars", mySeminarsList);
        return resourceManager.getProperty(Constants.PAGE_MY_SEMINARS);
    }

    @RequestMapping(value = "/unSubscribeSeminar", method = RequestMethod.GET)
    public String unSubscribeSeminar(ModelMap modelMap, HttpSession session, String seminarId){
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
    public String chooseSeminars(ModelMap modelMap, HttpSession session){
        String login = (String) session.getAttribute("user");
        List<SeminarsEntity> availableSeminarsList;
        availableSeminarsList = seminarService.availableSeminarsForLogin(login);
        session.setAttribute("availableSeminars", availableSeminarsList);
        return resourceManager.getProperty(Constants.PAGE_CHOOSE_SEMINARS);
    }

    @RequestMapping(value = "/subscribeToSeminar", method = RequestMethod.GET)
    public String subscribeToSeminar(ModelMap modelMap, HttpSession session, String seminarId){
        String login = (String) session.getAttribute("user");
        try {
            Integer sId = Integer.parseInt(seminarId);
            seminarService.subscribeToSeminar(modelMap, login, sId);
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать ID семинара.");
        }
        return chooseSeminars(modelMap, session);
    }

    @RequestMapping(value = "/showUsers", method = {RequestMethod.POST, RequestMethod.GET})
    public String showUsers(ModelMap modelMap, HttpSession session, String perPage,
                       @RequestParam(value = "page", required = false) String page,
                       @RequestParam(value = "sortBy", required = false) String sortBy,
                       @RequestParam(value = "orderType", required = false) String orderType,
                       @RequestParam(value = "filterRemove", required = false) String filterRemove,
                       @RequestParam(value = "filterSet", required = false) String filterSet,
                       @RequestParam(value = "filterText", required = false) String filterText){
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(/*request,*/ login)) {
            HashMap<String, String> filters = (HashMap<String, String>) session.getAttribute("filters");
            filters = filters == null ? new HashMap<>() : filters;
            if (filterSet != null && filterText != null && !filterText.equals("")) {
                filters.put(filterSet, filterText);
            }
            if (filterRemove != null) filters.remove(filterRemove);
            session.setAttribute("filters", filters);

            if (perPage != null) modelMap.addAttribute("perPage", perPage);
            if (page != null) modelMap.addAttribute("page", page);
            if (sortBy != null) session.setAttribute("sortBy", sortBy);
            if (orderType != null) session.setAttribute("orderType", orderType);
            if (filterRemove != null) modelMap.addAttribute("filterRemove", filterRemove);

            List<UsersEntity> userSet;
            userSet = userService.getAll(modelMap, session);
            modelMap.addAttribute("userSet", userSet);
            return resourceManager.getProperty(Constants.PAGE_SHOW_USERS);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/register", method = {RequestMethod.GET, RequestMethod.POST})
    public String register(ModelMap modelMap, HttpSession session,
                           @RequestParam(value = "newName", required = false) String newName,
                           @RequestParam(value = "newLastName", required = false) String newLastName,
                           @RequestParam(value = "newLogin", required = false) String newLogin,
                           @RequestParam(value = "newPassword", required = false) String newPassword,
                           @RequestParam(value = "repeatPassword", required = false) String repeatPassword
                           ){
        String login = (String) session.getAttribute("user");
        if (login == null || userService.isAdminUser(login)) {
            String page = resourceManager.getProperty(Constants.PAGE_REGISTRATION);
            UsersEntity regData;
            /**
             * creating attribute of the session: UsersEntity regData
             */
            if (session.getAttribute("regData") == null) {
                regData = new UsersEntity();
                session.setAttribute("regData", regData);
            }
            /**
             * reading data from session attribute regData
             */
            else {
                regData = (UsersEntity) session.getAttribute("regData");
//                regData = userService.setAllDataOfEntity(regData, request.getParameter("newName"), request.getParameter("newLastName"),
//                        request.getParameter("newLogin"), request.getParameter("newPassword"), false, false);
//                String repeatPassword = request.getParameter("repeatPassword");
                regData = userService.setAllDataOfEntity(regData, newName, newLastName,
                        newLogin, newPassword, false, false);
                /**
                 * if 'login' is entered
                 */
                if (regData.getLogin() != null && !"".equals(regData.getLogin())) {
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
                                page = resourceManager.getProperty(Constants.PAGE_REGISTRATION_OK);
                            } else {
                                modelMap.addAttribute(errorMessageAttribute, "Пароль должен содержать не менее 8 символов.");
                            }
                        }
                    }
                }
            }
            return page;
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/openedOrders", method = RequestMethod.GET)
    public String openedOrders(ModelMap modelMap, HttpSession session){
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
    public String waitingOrders(ModelMap modelMap, HttpSession session){
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
    public String provideBook(ModelMap modelMap, HttpSession session, String orderId){
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
    public String closeOrder(ModelMap modelMap, HttpSession session, String orderId){
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
    public String expiredOrders(ModelMap modelMap, HttpSession session){
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
                            @RequestParam(value = "login", required = false) String userLogin){
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
    public String blacklist(ModelMap modelMap, HttpSession session){
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
                              @RequestParam(value = "login", required = false) String userLogin){
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            userService.setBlockUser(userLogin, false);
            return blacklist(modelMap, session);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/searchBookAdmin", method = {RequestMethod.GET, RequestMethod.POST})
    public String searchBookAdmin(ModelMap modelMap, HttpSession session, String searchTextInBook){
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            if (searchTextInBook != null && !"".equals(searchTextInBook)) {
                List<BooksEntity> searchBookAdmin = bookService.searchBooksByTitleOrAuthor(searchTextInBook, login);
                session.setAttribute("searchBookAdmin", searchBookAdmin);
            }
            return resourceManager.getProperty(Constants.PAGE_SEARCH_BOOK_ADMIN);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/addBook", method = {RequestMethod.GET, RequestMethod.POST})
    public String addBook(ModelMap modelMap, HttpSession session, String newTitle, String newAuthor){
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            BooksEntity regData = (BooksEntity) session.getAttribute("regData");
            if (regData == null) {
                regData = new BooksEntity();
                session.setAttribute("regData", regData);
            } else {
                regData.setTitle(newTitle);
                regData.setAuthor(newAuthor);
                if (newTitle != null && newAuthor != null && !"".equals(newTitle) && !"".equals(newAuthor)) {
                    regData.setIsBusy(false);
                    bookService.add(regData);
                    modelMap.addAttribute(infoMessageAttribute, "Книга добавлена в базу библиотеки.");
                    regData.setTitle("");
                    regData.setAuthor("");
                } else if (newTitle != null && newAuthor != null) {
                    modelMap.addAttribute(errorMessageAttribute, "Поля данных книги не должны быть пустыми.");
                }
            }
            return resourceManager.getProperty(Constants.PAGE_ADD_BOOK);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/deleteBook", method = RequestMethod.GET)
    public String deleteBook(ModelMap modelMap, HttpSession session, String bookId){
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
    public String adminSeminars(ModelMap modelMap, HttpSession session){
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

    @RequestMapping(value = "/addSeminar", method = {RequestMethod.GET, RequestMethod.POST})
    public String addSeminar(ModelMap modelMap, HttpSession session, String newSubject, String newDate){
        String login = (String) session.getAttribute("user");
        if (userService.isAdminUser(login)) {
            SeminarsEntity regData = (SeminarsEntity) session.getAttribute("regData");
            if (regData == null) {
                regData = new SeminarsEntity();
                session.setAttribute("regData", regData);
            } else {
                Date date = null;
                if (!"".equals(newDate)) {
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(newDate);
                    } catch (ParseException e) {/*NOP*/}
                }
                Date today1 = new Date();
                Date today = new Date(today1.getYear(), today1.getMonth(), today1.getDate());
                if (!"".equals(newSubject) && date != null && (today.before(date) || today.equals(date))) {
                    regData.setSubject(newSubject);
                    regData.setSeminarDate(date);
                    seminarService.add(regData);
                    modelMap.addAttribute(infoMessageAttribute, "Семинар добавлен в базу.");
                    session.removeAttribute("regData");
                } else {
                    modelMap.addAttribute(errorMessageAttribute, "Поля данных семинара не должны быть пустыми и дата должна быть >= сегодня.");
                }
            }
            return resourceManager.getProperty(Constants.PAGE_ADD_SEMINAR);
        } else if ((modelMap.get(errorMessageAttribute)) == null) {
            modelMap.addAttribute(errorMessageAttribute, "У Вас нет прав для выполнения данной команды.");
        }
        return redirectToMainPage(login);
    }

    @RequestMapping(value = "/usersOfSeminar", method = RequestMethod.GET)
    public String usersOfSeminar(ModelMap modelMap, HttpSession session, String seminarId){
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
    public String deleteSeminar(ModelMap modelMap, HttpSession session, String seminarId){
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
                          @PathVariable String path){
        modelMap.addAttribute(errorMessageAttribute, "Команда не распознана или не задана!");
        return resourceManager.getProperty(Constants.PAGE_ERROR);
    }

    private /*void*/ String redirectToMainPage(String login) {
        String page;
        if (userService.isAdminUser(login)) {
            page = resourceManager.getProperty(Constants.PAGE_MAIN_ADMIN);
        } else {
            page = resourceManager.getProperty(Constants.PAGE_MAIN);
        }
//        setForwardPage(page);
        return page;
    }

    private /*void*/ String redirectToLoginPage() {
        return resourceManager.getProperty(Constants.PAGE_INDEX);
    }
}
