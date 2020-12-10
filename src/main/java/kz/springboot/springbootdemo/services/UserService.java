package kz.springboot.springbootdemo.services;

import kz.springboot.springbootdemo.entities.Roles;
import kz.springboot.springbootdemo.entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    Users getUserByEmail(String email);
    Users getUserById(Long id);
    boolean createUser(Users user);
    boolean saveUser1(Users user);
    boolean saveUser2(Users user,String new_password,String old_password,boolean access);
    List<Users> getAllByOrderByIdAsc();
    Roles getRoleById(Long id);
    void saveUser(Users user);
    List<Roles> findAllRoles();
}
