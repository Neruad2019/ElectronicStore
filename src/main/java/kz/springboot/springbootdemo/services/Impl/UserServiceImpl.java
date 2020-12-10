package kz.springboot.springbootdemo.services.Impl;

import kz.springboot.springbootdemo.entities.Roles;
import kz.springboot.springbootdemo.entities.Users;
import kz.springboot.springbootdemo.repositories.RoleRepository;
import kz.springboot.springbootdemo.repositories.UserRepository;
import kz.springboot.springbootdemo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users myUser=userRepository.findByEmail(s);
            if(myUser!=null){
            User secUser=new User(myUser.getEmail(),myUser.getPassword(),myUser.getRoles());
            return secUser;
        }
        throw new UsernameNotFoundException("User Not Found");
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean createUser(Users user) {
        Users checkUser=userRepository.findByEmail(user.getEmail());
        if(checkUser==null){
            Roles role=roleRepository.findByName("ROLE_USER");
            if(role!=null){
                ArrayList<Roles> roles=new ArrayList<>();
                roles.add(role);
                user.setRoles(roles);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean saveUser1(Users user) {
        if(user!=null){
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean saveUser2(Users user, String new_password, String old_password,boolean access) {
        if(access || passwordEncoder.matches(old_password,user.getPassword())){
            user.setPassword(passwordEncoder.encode(new_password));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public Users getUserById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public List<Users> getAllByOrderByIdAsc() {
        return userRepository.getAllByOrderByIdAsc();
    }

    @Override
    public Roles getRoleById(Long id) {
        return roleRepository.getOne(id);
    }

    @Override
    public void saveUser(Users user) {
        userRepository.save(user);
    }

    @Override
    public List<Roles> findAllRoles() {
        return roleRepository.getAllByOrderByIdAsc();
    }
}
