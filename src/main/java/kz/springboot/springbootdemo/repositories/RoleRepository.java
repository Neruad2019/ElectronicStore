package kz.springboot.springbootdemo.repositories;

import kz.springboot.springbootdemo.entities.Roles;
import kz.springboot.springbootdemo.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Roles, Long> {

    Roles findByName(String name);
    List<Roles> getAllByOrderByIdAsc();

}
