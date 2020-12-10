package kz.springboot.springbootdemo.repositories;

import kz.springboot.springbootdemo.entities.Brands;
import kz.springboot.springbootdemo.entities.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ItemRepository extends JpaRepository<Items, Long> {

    List<Items> findAllByInTopPageEqualsOrderByPriceDesc(boolean d);

    List<Items> findAllByNameContainingAndPriceBetweenOrderByPriceAsc(String name, double from, double to);

    List<Items> findAllByNameContainingAndPriceBetweenOrderByPriceDesc(String name, Double from, Double to);

    List<Items> findAllByNameContainingAndBrandEqualsAndPriceBetweenOrderByPriceAsc(String name, Brands id, double from, double to);

    List<Items> findAllByNameContainingAndBrandEqualsAndPriceBetweenOrderByPriceDesc(String name, Brands id, Double from, Double to);
}
