package com.petko.services;

import com.petko.ActiveUsers;
import com.petko.DaoException;
import com.petko.dao.IUserDao;
import com.petko.entities.UsersEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class UserService implements IUserService {
    private static Logger log = Logger.getLogger(UserService.class);
    @Autowired
    private IUserDao userDao;

    /**
     * Adds into active users List
     *
     * @param login to be added
     */
    private void addToActiveUsers(String login) {
        ActiveUsers.addUser(login);
    }

    /**
     * Removes from active users List
     *
     * @param login to be removed
     */
    private void removeFromActiveUsers(String login) {
        ActiveUsers.removeUser(login);
    }

    /**
     * closes request session and removes user from active users list
     *
     * @param session - current http session
     * @param login   - user to be logOut
     */
    public void logOut(HttpSession session, String login) {
        if (login != null) removeFromActiveUsers(login);
        session.invalidate();
    }

    /**
     * Checks if user is already logged in
     * //     * @param request - current http request
     *
     * @param login    to be checked
     * @param password to be checked
     * @return success or not
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public boolean isLoginSuccess(String login, String password) {
        if (login == null || password == null) return false;
        try {
            UsersEntity user = userDao.getByLogin(login);
            log.info("Get user by login (commit)");
            if (user != null && password.equals(user.getPassword())) {
                addToActiveUsers(login);
                return true;
            } else {
                return false;
            }
        } catch (DaoException e) {
            return false;
        }
    }

    /**
     * Checks if the User is Admin or not
     * @param login to be checked
     * @return admin or not (true or false)
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public boolean isAdminUser(String login) {
        try {
            UsersEntity user = userDao.getByLogin(login);
            if (user != null) return user.getIsAdmin();
            else return false;
        } catch (DaoException e) {
            return false;
        }
    }

    /**
     * gives List of all Users. According to filters, sorts and current page
     *
     * @param modelMap - Map of current http model
     * @param session - current httpSession
     * @return List of all Users
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public List<UsersEntity> getAll(ModelMap modelMap, HttpSession session) {
        List<UsersEntity> result;
        String page = (String) modelMap.get("page");

        // for sorting and filtering
        String  sortBy = (String) session.getAttribute("sortBy");
        String  orderType = (String) session.getAttribute("orderType");
        Map<String, String> filters = (Map<String, String>) session.getAttribute("filters");

        String perPageString = (String) modelMap.get("perPage");
        Integer newPerPage = perPageString != null ? Integer.parseInt(perPageString) : null;
        Integer oldPerPage = (Integer) session.getAttribute("max");
        Integer newMax;
        if (newPerPage != null) {
            newMax = newPerPage;
        }
        else newMax = oldPerPage != null ? oldPerPage : 5;

        try {
            int firstInt;
            if (page == null) {
                Long total = userDao.getTotal();
                log.info("getTotal users (commit)");
                session.setAttribute("total", total);
                firstInt = 0;
            } else {
                Integer pageInt = Integer.parseInt(page);
                Integer newPageInt = getPageDueToNewPerPage(modelMap, session, pageInt, newPerPage, oldPerPage);
                if (sortBy != null && orderType != null) {
                    session.setAttribute("sortBy", sortBy);
                    session.setAttribute("orderType", orderType);
                }
                firstInt = (newPageInt - 1) * newMax;
            }
            Long totalLong = (Long) session.getAttribute("total");
            int total = totalLong.intValue();
            session.setAttribute("totalToShow", userDao.getAll(0, total, sortBy, orderType, filters).size());
            result = userDao.getAll(firstInt, newMax, sortBy, orderType, filters);
            session.setAttribute("max", newMax);
            log.info("getAll users (commit)");
        } catch (DaoException e) {
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * Re-estimates current page due to perPage parameter changes
     *
     * @param modelMap    - current httpRequest map
     * @param session    - current session
     * @param page       - current page (not re-estimated)
     * @param newPerPage - a new perPage parameter
     * @param oldPerPage - perPage parameter which is saved in session
     * @return the correct page due to perPage changes
     */
    private Integer getPageDueToNewPerPage(ModelMap modelMap, HttpSession session, Integer page,
                                           Integer newPerPage, Integer oldPerPage) {
        Integer result;
        Integer totalToShow = (Integer) session.getAttribute("totalToShow");
        if ((newPerPage == null || oldPerPage == null) || (newPerPage.equals(oldPerPage))) {
            result = page;
        } else if (newPerPage > oldPerPage) {
            result = changeAndGiveCurrentPage(page, totalToShow, newPerPage, oldPerPage, true);
        } else {
            result = changeAndGiveCurrentPage(page, totalToShow, newPerPage, oldPerPage, false);
        }
        modelMap.addAttribute("page", result);
        return result;
    }

    /**
     * Re-estimates current page due to perPage parameter changes
     * @param page          - current page (not re-estimated)
     * @param totalToShow   - the whole amount of records (including filters)
     * @param newPerPage    - a new perPage parameter
     * @param oldPerPage    - perPage parameter which is saved in session
     * @param isMoreRecords - is new perPage bigger than current
     * @return the correct page due to perPage changes
     */
    private Integer changeAndGiveCurrentPage(Integer page, Integer totalToShow, Integer newPerPage,
                                             Integer oldPerPage, boolean isMoreRecords) {
        Integer result;
        int temp = page * oldPerPage / newPerPage;
        int rest = page * oldPerPage % newPerPage;
        if (isMoreRecords) {
            result = rest != 0 ? temp + 1 : temp;
        } else result = rest != 0 ? temp - 1 : temp;
        rest = totalToShow % newPerPage;
        Integer endPage = rest != 0 ? (totalToShow - rest) / newPerPage + 1 : totalToShow / newPerPage;
        if (endPage < result) result = endPage;
        return result;
    }

    /**
     * Checks if login exists in DataBase
     *
     * @param login   to be checked
     * @return exist or not (true or false)
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public boolean isLoginExists(String login) {
        boolean result = false;
        try {
            UsersEntity userEntity = userDao.getByLogin(login);
            String entityLogin = null;
            if (userEntity != null) entityLogin = userEntity.getLogin();
            if (login != null && login.equals(entityLogin)) result = true;
            log.info("Get user by login (commit)");
        } catch (DaoException e) {
        }
        return result;
    }

    /**
     * check for equality and the length
     *
     * @param password       to be checked
     * @param repeatPassword to check for equality with password
     * @return true or false
     */
    public boolean isAllPasswordRulesFollowed(String password, String repeatPassword) {
        return password != null && password.equals(repeatPassword) && password.length() >= 8;
    }

    /**
     * Adds User to DataBase
     *
     * @param entity  to be added
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public void add(UsersEntity entity) {
        try {
            userDao.save(entity);
            log.info("Save user to DB (commit)");
        } catch (DaoException e) {
        }
    }

    /**
     * Changes the status of the User
     *
     * @param login     to be updeted
     * @param isBlocked - status to be setted
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public void setBlockUser(String login, boolean isBlocked) {
        try {
            UsersEntity entity = userDao.getByLogin(login);
            if (entity != null) {
                entity.setIsBlocked(isBlocked);
                userDao.update(entity);
            }
            log.info("Get user by login (commit)");
            log.info("Update user (commit)");
        } catch (DaoException e) {
        }
    }

    /**
     * Gets Users by their status
     *
     * @param isBlocked - status to be searched
     * @return Users by their status
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public List<UsersEntity> getUsersByBlock(boolean isBlocked) {
        List<UsersEntity> allByBlock;
        try {
            allByBlock = userDao.getAllByBlockStatus(isBlocked);
            log.info("Get all users by block status (commit)");
            return allByBlock;
        } catch (DaoException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Checks if all register data is entered
     *
     * @param regData        - all data to be checked
     * @param repeatPassword to compare with password
     * @return true or false
     */
    public boolean isAllRegisterDataEntered(UsersEntity regData, String repeatPassword) {
        return regData != null &&
                regData.getFirstName() != null &&
                !"".equals(regData.getFirstName()) &&
                regData.getLastName() != null &&
                !"".equals(regData.getLastName()) &&
                regData.getLogin() != null &&
                !"".equals(regData.getLogin()) &&
                regData.getPassword() != null &&
                !"".equals(regData.getPassword()) &&
                repeatPassword != null &&
                !"".equals(repeatPassword);
    }

    /**
     * removes Book by book Id
     *
     * @param request - current request
     * @param userId  - id of the User to be deleted
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public UsersEntity deleteUser(HttpServletRequest request, Integer userId) {
        UsersEntity user;
        try {
            user = userDao.getById(userId);
            if (user != null) {
                userDao.delete(user);
                log.info("Delete user (commit)");
            }
        } catch (DaoException e) {
            return null;
        }
        return user;
    }

    /**
     * Creates and gives a new User
     *
     * @param result    - result
     * @param firstName - firstName
     * @param lastName  - lastName
     * @param login     - login
     * @param password  - password
     * @param isAdmin   - isAdmin
     * @param isBlocked - isBlocked
     * @return a new User
     */
    public UsersEntity setAllDataOfEntity(UsersEntity result, String firstName, String lastName, String login, String password,
                                          boolean isAdmin, boolean isBlocked) {
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setLogin(login);
        result.setPassword(password);
        result.setIsAdmin(isAdmin);
        result.setIsBlocked(isBlocked);
        return result;
    }
}
