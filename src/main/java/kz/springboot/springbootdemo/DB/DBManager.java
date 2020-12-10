package kz.springboot.springbootdemo.DB;

import java.util.ArrayList;

public class DBManager {
    private static ArrayList<ShopItem> shopItems=new ArrayList<>();
    private static Long id=4l;

    static{
        shopItems.add(new ShopItem(1l,"Samsung Galaxy S20 FE 5G","The Samsung Galaxy S20 FE 5G delivers all the phone performance most people need at a price more people can afford.",680,23,3,"https://i.pcmag.com/imagery/reviews/03xdTO0Ka4H4KvEgtSPg4c2-12.1569479325.fit_lpad.size_298x174.jpg"));
        shopItems.add(new ShopItem(2l,"Apple iPhone 11 Pro","The iPhone 11 offers solid camera performance and power to spare for a relatively reasonable price. It isn't exciting, but it's a good replacement for older iPhones.",600,14,4,"https://i.pcmag.com/imagery/reviews/04R1s9xuQfmVH4MHFeuaghc-18.1570065414.fit_lpad.size_298x174.jpg"));
        shopItems.add(new ShopItem(3l,"Samsung Galaxy Note 20 Ultra","The Samsung Galaxy Note 20 Ultra delivers the most high-end phone experience money can buy in a year when money is tighter than ever.",1100,17,4,"https://i.pcmag.com/imagery/reviews/02YqCzRGHlaPPIzfK5IcnGV-13.1597409139.fit_lpad.size_298x174.jpg"));
    }

    public static void AddItem(ShopItem shopItem){
        shopItem.setId(id);
        shopItems.add(shopItem);
        id++;
    }
    public static ArrayList<ShopItem> getShopItems(){
        return shopItems;
    }
    public static ShopItem getItemByID(Long id){
        for (ShopItem item:shopItems) {
            if(item.getId()==id){
                return item;
            }
        }
        return null;
    }
}
