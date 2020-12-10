package kz.springboot.springbootdemo.repositories;

import kz.springboot.springbootdemo.entities.Countries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Countries, Long> {
}
