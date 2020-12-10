package kz.springboot.springbootdemo.services;

import kz.springboot.springbootdemo.entities.Brands;
import kz.springboot.springbootdemo.entities.Categories;
import kz.springboot.springbootdemo.entities.Countries;
import kz.springboot.springbootdemo.entities.Items;

import java.util.List;

public interface ItemService {
    void addItem(Items item);
    List<Items> getAllItems();
    Items getItem(Long id);
    void deleteItemById(Long id);
    void saveItem(Items item);
    List<Items> getAllItemsFirstlyTop();
    List<Items> getSearchItemsAsc(String name,double from,double to);
    List<Items> getSearchItemsDesc(String name,double from,double to);

    List<Countries> getAllCountries();
    Countries getCountry(Long id);
    void addCountry(Countries countries);
    void deleteCountry(Long id);
    void saveCountry(Countries countries);

    List<Categories> getAllCategories();
    Categories getCategory(Long id);
    void addCategory(Categories categories);
    void deleteCategory(Long id);
    void saveCategory(Categories categories);

    List<Brands> getAllBrands();
    Brands getBrand(Long id);
    void addBrand(Brands brands);
    void deleteBrand(Long id);
    void saveBrand(Brands brands);

    List<Items> getSearchBrandItemsAsc(String name,Brands id,double from,double to);
    List<Items> getSearchBrandItemsDesc(String name,Brands id,double from,double to);
}
