package kz.springboot.springbootdemo.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "Purchase_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Purchase_history {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="item_id")
    private Long item_id;

    @Column(name="amount")
    private Long amount;

    @Column(name="added_date")
    private Date addedDate;
}
