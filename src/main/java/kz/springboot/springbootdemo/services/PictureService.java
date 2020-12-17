package kz.springboot.springbootdemo.services;

import kz.springboot.springbootdemo.entities.Items;
import kz.springboot.springbootdemo.entities.Pictures;

import java.util.List;

public interface PictureService {

    void deletePicture(Long id);
    void savePicture(Pictures pictures);
    List<Pictures> getAllpicturesbByItem(Items item);
    List<Pictures> getAll();

}
