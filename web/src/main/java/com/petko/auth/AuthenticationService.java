package com.petko.auth;

import com.petko.DaoException;
import com.petko.dao.IUserDao;
import com.petko.entities.UsersEntity;
import com.petko.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

//@Service("authService")
@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserDao userDao;

    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public UserDetails loadUserByUsername(String userName)
            throws UsernameNotFoundException {
        try {
            UsersEntity user = userDao.getByLogin(userName);
            System.out.println("User : " + user);
            if (user == null) {
                System.out.println("User not found");
                throw new UsernameNotFoundException("Username not found");
            }
            return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                    user.getIsAdmin(), true, true, true, getGrantedAuthorities(user));
        } catch (DaoException e) {
            return null;
        }
    }


    private List<GrantedAuthority> getGrantedAuthorities(UsersEntity user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

//        for (UserProfile userProfile : user.getUserProfiles()) {
//            System.out.println("UserProfile : " + userProfile);
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getIsAdmin()));
//        }
        System.out.print("authorities :" + authorities);
        return authorities;
    }

}
