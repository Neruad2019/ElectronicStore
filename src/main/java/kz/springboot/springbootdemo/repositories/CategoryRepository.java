package kz.springboot.springbootdemo.repositories;

import kz.springboot.springbootdemo.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Categories,Long> {
}
