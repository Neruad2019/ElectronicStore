package kz.springboot.springbootdemo.repositories;

import kz.springboot.springbootdemo.entities.Items;
import kz.springboot.springbootdemo.entities.Pictures;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PictureRepository extends JpaRepository<Pictures, Long> {
    List<Pictures> getAllByItemOrderByAddedDateDesc(Items item);
    List<Pictures> getAllByOrderByIdAsc();
}
