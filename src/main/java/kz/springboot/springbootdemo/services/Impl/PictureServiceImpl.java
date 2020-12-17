package kz.springboot.springbootdemo.services.Impl;

import kz.springboot.springbootdemo.entities.Items;
import kz.springboot.springbootdemo.entities.Pictures;
import kz.springboot.springbootdemo.repositories.PictureRepository;
import kz.springboot.springbootdemo.services.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {
    @Autowired
    private PictureRepository pictureRepository;

    @Override
    public void deletePicture(Long id) {
        pictureRepository.deleteById(id);
    }

    @Override
    public void savePicture(Pictures pictures) {
        pictureRepository.save(pictures);
    }

    @Override
    public List<Pictures> getAllpicturesbByItem(Items item) {
        return pictureRepository.getAllByItemOrderByAddedDateDesc(item);
    }

    @Override
    public List<Pictures> getAll() {
        return pictureRepository.getAllByOrderByIdAsc();
    }
}
