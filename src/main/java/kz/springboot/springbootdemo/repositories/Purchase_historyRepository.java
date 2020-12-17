package kz.springboot.springbootdemo.repositories;

import kz.springboot.springbootdemo.entities.Purchase_history;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface Purchase_historyRepository extends JpaRepository<Purchase_history, Long> {
    List<Purchase_history> getAllByOrderByIdDesc();
}
