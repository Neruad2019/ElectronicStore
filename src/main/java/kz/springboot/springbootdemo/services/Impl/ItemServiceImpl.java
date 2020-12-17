package kz.springboot.springbootdemo.services.Impl;

import kz.springboot.springbootdemo.entities.*;
import kz.springboot.springbootdemo.repositories.*;
import kz.springboot.springbootdemo.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private Purchase_historyRepository purchase_historyRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void addItem(Items item) {
         itemRepository.save(item);
    }

    @Override
    public List<Items> getAllItemsFirstlyTop() {
        List<Items> Items = new ArrayList<>();
        Items.addAll(itemRepository.findAllByInTopPageEqualsOrderByPriceDesc(true));
        Items.addAll(itemRepository.findAllByInTopPageEqualsOrderByPriceDesc(false));
        return Items;
    }

    @Override
    public List<Items> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Items getItem(Long id) {
        return itemRepository.getOne(id);
    }

    @Override
    public void deleteItemById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public void saveItem(Items item) {
         itemRepository.save(item);
    }

    @Override
    public List<Items> getSearchItemsAsc(String name, double from, double to) {
        return itemRepository.findAllByNameContainingAndPriceBetweenOrderByPriceAsc(name,from,to);
    }

    @Override
    public List<Items> getSearchItemsDesc(String name, double from, double to) {
        return itemRepository.findAllByNameContainingAndPriceBetweenOrderByPriceDesc(name,from,to);
    }

    @Override
    public List<Items> getSearchBrandItemsAsc(String name, Brands id, double from, double to) {
        return itemRepository.findAllByNameContainingAndBrandEqualsAndPriceBetweenOrderByPriceAsc(name,id,from,to);
    }

    @Override
    public List<Items> getSearchBrandItemsDesc(String name, Brands id, double from, double to) {
        return itemRepository.findAllByNameContainingAndBrandEqualsAndPriceBetweenOrderByPriceDesc(name,id,from,to);
    }

    @Override
    public List<Countries> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Countries getCountry(Long id) {
        return countryRepository.getOne(id);
    }

    @Override
    public void addCountry(Countries countries) {
        countryRepository.save(countries);
    }

    @Override
    public void deleteCountry(Long id) {
        countryRepository.deleteById(id);
    }

    @Override
    public void saveCountry(Countries countries) {
        countryRepository.save(countries);
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Brands getBrand(Long id) {
        return brandRepository.getOne(id);
    }

    @Override
    public void addBrand(Brands brands) {
        brandRepository.save(brands);
    }

    @Override
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }

    @Override
    public void saveBrand(Brands brands) {
        brandRepository.save(brands);
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Categories getCategory(Long id) {
        return categoryRepository.getOne(id);
    }

    @Override
    public void addCategory(Categories categories) {
        categoryRepository.save(categories);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void saveCategory(Categories categories) {
        categoryRepository.save(categories);
    }

    @Override
    public List<Purchase_history> getAllPurchases() {
        return purchase_historyRepository.getAllByOrderByIdDesc();
    }

    @Override
    public void addPurchase(Purchase_history purchase_history) {
        purchase_historyRepository.save(purchase_history);
    }

    @Override
    public List<Comments> getAllCommentsbyItem(Items item) {
        return commentRepository.getAllByItemEqualsOrderByIdDesc(item);
    }

    @Override
    public void addComment(Comments comment) {
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public void saveComment(Comments comment) {
        commentRepository.save(comment);
    }

    @Override
    public Comments getOneComment(Long id) {
        return commentRepository.getOne(id);
    }
}
