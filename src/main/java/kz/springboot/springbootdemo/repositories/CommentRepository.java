package kz.springboot.springbootdemo.repositories;

import kz.springboot.springbootdemo.entities.Comments;
import kz.springboot.springbootdemo.entities.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CommentRepository extends JpaRepository<Comments, Long> {
    List<Comments> getAllByItemEqualsOrderByIdDesc(Items item);
}
