package kz.springboot.springbootdemo.repositories;

import kz.springboot.springbootdemo.entities.Brands;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brands,Long> {
}
