package kz.springboot.springbootdemo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Items {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "stars")
    private int stars;

    @Column(name = "amount")
    private int amount;

    @Column(name = "smallPicURL")
    private String smallPicURL;

    @Column(name = "largePicURL")
    private String largePicURL;

    @Column(name = "addedDate")
    private Date addedDate;

    @Column(name = "inTopPage")
    private boolean inTopPage;

    @ManyToOne(fetch = FetchType.EAGER)
    private Brands brand;

    @ManyToMany(fetch = FetchType.EAGER)
    List<Categories> categories;

}
