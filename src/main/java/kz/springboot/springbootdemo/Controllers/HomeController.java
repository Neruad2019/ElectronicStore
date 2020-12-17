package kz.springboot.springbootdemo.Controllers;

import kz.springboot.springbootdemo.entities.*;
import kz.springboot.springbootdemo.repositories.PictureRepository;
import kz.springboot.springbootdemo.services.ItemService;
import kz.springboot.springbootdemo.services.PictureService;
import kz.springboot.springbootdemo.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UserService userService;

    @Value("${file.avatar.viewPath}")
    private String viewPath;
    @Value("${file.avatar.uploadPath}")
    private String uploadPath;
    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;

    @Value("${file.itemphoto.viewPath}")
    private String itemphotoviewPath;
    @Value("${file.itemphoto.uploadPath}")
    private String itemphotouploadPath;

    @GetMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("currentUser", getUserData());
        List<Items> shopItems = itemService.getAllItemsFirstlyTop();
        model.addAttribute("Items", shopItems);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        return "index";
    }

    //-------------------------

    @GetMapping(value = "/login")
    public String Login(Model model) {
        List<Items> shopItems = itemService.getAllItemsFirstlyTop();
        model.addAttribute("Items", shopItems);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        return "Login";
    }

    @GetMapping(value = "/register")
    public String Register() {

        return "Register";
    }

    @PostMapping(value = "/register")
    public String toRegister(Model model,
            @RequestParam(name = "user_email") String user_email,
                             @RequestParam(name = "user_password") String user_password,
                             @RequestParam(name = "user_full_name") String user_full_name,
                             @RequestParam(name = "byadmin",defaultValue = "false") boolean access,
                             @RequestParam(name = "re_user_password") String re_user_password) {
        if (user_password.equals(re_user_password)) {
            Users users = new Users();
            users.setFullName(user_full_name);
            users.setPassword(user_password);
            users.setEmail(user_email);
            if (userService.createUser(users)) {
                if(access){
                    List<Users> userss=userService.getAllByOrderByIdAsc();
                    model.addAttribute("Users",userss);
                    return "adminpanel_User";
                }
                return "redirect:/register?success";
            }
        }
        if(access){
            List<Users> userss=userService.getAllByOrderByIdAsc();
            model.addAttribute("Users",userss);
            return "adminpanel_User";
        }
        System.out.println("Err3------------------------------------");
        return "redirect:/register?error";
    }

    @PostMapping(value = "/updateprofile")
    public String updateprofile(@RequestParam(name = "user_email") String user_email,
                                @RequestParam(name = "id") Long id,
                                @RequestParam(name = "user_full_name") String user_full_name) {
        Users checkUser = userService.getUserById(id);
        if (checkUser != null) {
            checkUser.setEmail(user_email);
            checkUser.setFullName(user_full_name);
            if(userService.saveUser1(checkUser)){
                return "redirect:/profile?success";
            }
        }
        return "redirect:/profile?error";
    }

    @PostMapping(value = "/updateavatar")
    @PreAuthorize("isAuthenticated()")
    public String updateAvatar(@RequestParam(name = "user_ava")MultipartFile file){
        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image.png")){
        try {
            Users currentUser=getUserData();
            String picName= DigestUtils.sha1Hex("avatar_"+currentUser.getId()+"_Picture");
            byte[] bytes=file.getBytes();
            Path path= Paths.get(uploadPath+picName+".jpg");
            Files.write(path,bytes);
            currentUser.setPictureURL(picName);
            userService.saveUser(currentUser);
            return "redirect:/profile?success";
        }catch (Exception ex){
            ex.printStackTrace();
        }}
        return "redirect:/profile?error";
    }
    @GetMapping(value = "/viewphoto/{url}",produces = {MediaType.IMAGE_JPEG_VALUE})
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody byte[] viewProfilePhoto(@PathVariable(name = "url") String url) throws IOException{
        String pictureURL=viewPath+defaultPicture;
        if(url!=null){
            pictureURL=viewPath+url+".jpg";
        }
        InputStream in;
        try {
            ClassPathResource resource=new ClassPathResource(pictureURL);
            in=resource.getInputStream();
        }catch (Exception ex){
            ClassPathResource resource=new ClassPathResource(viewPath+defaultPicture);
            in=resource.getInputStream();
            ex.printStackTrace();
        }
        return IOUtils.toByteArray(in);
    }

    @PostMapping(value = "/uploaditemphoto")
    @PreAuthorize("isAuthenticated()")
    public String uploaditemphoto(Model model,
                                  @RequestParam(name = "item_picture")MultipartFile file,
                                  @RequestParam(name = "item_id") Long id){
        List<Items> shopItems = itemService.getAllItems();
        model.addAttribute("Items", shopItems);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("Categories", categoriesList);
        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image.png")){
            try {
                List<Pictures> pictures=pictureService.getAll();
                Items item=itemService.getItem(id);
                long picNum;
                if(pictures.isEmpty()){
                    picNum=0;
                }
                else {
                    picNum=pictures.get(pictures.size()-1).getId()+1;
                }
                String picName= DigestUtils.sha1Hex("item_"+item.getId()+"_Picture"+picNum);
                byte[] bytes=file.getBytes();
                Path path= Paths.get(itemphotouploadPath+picName+".jpg");
                Files.write(path,bytes);

                Pictures picture=new Pictures();
                picture.setAddedDate(new Date(Calendar.getInstance().getTimeInMillis()));
                picture.setUrl(picName);
                picture.setItem(item);

                pictureService.savePicture(picture);

                List<Pictures> picturess=pictureService.getAllpicturesbByItem(item);
                model.addAttribute("Pictures",picturess);
                model.addAttribute("item", item);
                return "adminpanel_ItemDetails";

            }catch (Exception ex){
                ex.printStackTrace();
            }}
        System.out.println("ERR------------------------------");
        Items item=itemService.getItem(id);
        model.addAttribute("item", item);
        List<Pictures> picturess=pictureService.getAllpicturesbByItem(item);
        model.addAttribute("Pictures",picturess);
        return "adminpanel_ItemDetails";
    }
    @GetMapping(value = "/viewitemphoto/{url}",produces = {MediaType.IMAGE_JPEG_VALUE})
    //@PreAuthorize("isAuthenticated()")
    public @ResponseBody byte[] viewitemphoto(@PathVariable(name = "url") String url) throws IOException{
        String pictureURL=viewPath+defaultPicture;
        if(url!=null){
            pictureURL=itemphotoviewPath+url+".jpg";
        }
        InputStream in;
        try {
            ClassPathResource resource=new ClassPathResource(pictureURL);
            in=resource.getInputStream();
        }catch (Exception ex){
            ClassPathResource resource=new ClassPathResource(viewPath+defaultPicture);
            in=resource.getInputStream();
            ex.printStackTrace();
        }
        return IOUtils.toByteArray(in);
    }
    @PostMapping(value = "/deleteitemphoto")
    public String deleteitemphoto(Model model,
                                 @RequestParam(name = "item_id") Long item_id,
                                 @RequestParam(name = "picture_id") Long picture_id) {
        pictureService.deletePicture(picture_id);

        Items items = itemService.getItem(item_id);
        model.addAttribute("item", items);
        List<Items> shopItems = itemService.getAllItems();
        model.addAttribute("Items", shopItems);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("Categories", categoriesList);
        List<Pictures> pictures=pictureService.getAllpicturesbByItem(items);
        model.addAttribute("Pictures",pictures);
        return "adminpanel_ItemDetails";
    }


    @PostMapping(value = "/updatepassword")
    public String updatepassword(@RequestParam(name = "old_password") String old_password,
                                 @RequestParam(name = "id") Long id,
                                 @RequestParam(name = "new_password") String new_password,
                                 @RequestParam(name = "re_new_password") String re_new_password) {
        Users checkUser = userService.getUserById(id);
        if (checkUser != null) {
            if (new_password.equals(re_new_password)) {
                if(userService.saveUser2(checkUser,new_password,old_password,false)){
                    return "redirect:/profile?success";
                }
            }
        }
        return "redirect:/profile?error";
    }

    @PostMapping(value = "/updatepasswordByAdmin")
    public String updatepasswordByAdmin(Model model,
                                        @RequestParam(name = "old_password") String old_password,
                                 @RequestParam(name = "id") Long id,
                                 @RequestParam(name = "new_password") String new_password,
                                 @RequestParam(name = "re_new_password") String re_new_password) {
        Users checkUser = userService.getUserById(id);
        Users user = userService.getUserById(id);
        model.addAttribute("AllRoles",userService.findAllRoles());
        model.addAttribute("User",user);
        if (checkUser != null) {
            if (new_password.equals(re_new_password)) {
                if(userService.saveUser2(checkUser,new_password,"",true)){
                    model.addAttribute("condition","success");
                    return "adminpanel_UserDetails";
                }
            }
        }
        model.addAttribute("condition","error");
        return "adminpanel_UserDetails";
    }

    @RequestMapping(value = "/updateprofileByAdmin",method = { RequestMethod.POST, RequestMethod.GET })
    public String updateprofileByAdmin(Model model,
                                       @RequestParam(name = "user_email") String user_email,
                                @RequestParam(name = "id") Long id,
                                @RequestParam(name = "user_full_name") String user_full_name) {
        Users checkUser = userService.getUserById(id);
        Users user = userService.getUserById(id);
        model.addAttribute("User",user);
        model.addAttribute("AllRoles",userService.findAllRoles());
        if (checkUser != null) {
            checkUser.setEmail(user_email);
            checkUser.setFullName(user_full_name);
            if(userService.saveUser1(checkUser)){
                model.addAttribute("condition","success");
                return "adminpanel_UserDetails";
            }
        }
        model.addAttribute("condition","error");
        return "adminpanel_UserDetails";
    }
    @RequestMapping(value = "/adminpanelUsersDetails/{id}")
    public String UserDetails(Model model, @PathVariable(name = "id") Long id) {
        Users user = userService.getUserById(id);
        model.addAttribute("User",user);
        model.addAttribute("AllRoles",userService.findAllRoles());
        return "adminpanel_UserDetails";
    }

    @PostMapping(value = "/removerole")
    public String removerole(Model model,
                                 @RequestParam(name = "id") Long id,
                                 @RequestParam(name = "role_id") Long role_id) {

        Users user = userService.getUserById(id);
        model.addAttribute("User",user);
        Roles roles = userService.getRoleById(role_id);
        if (roles != null) {
            if (user != null) {
                List<Roles> rolesList = user.getRoles();
                if (rolesList == null) {
                    rolesList = new ArrayList<>();
                }
                rolesList.remove(roles);
                model.addAttribute("AllRoles",userService.findAllRoles());
                //items.setCategories(categoriesList);
                userService.saveUser(user);
            }
        }
        return "adminpanel_UserDetails";
    }

    @PostMapping(value = "/assignrole")
    public String assignrole(Model model,
                             @RequestParam(name = "id") Long id,
                             @RequestParam(name = "role_id") Long role_id) {
        Users user = userService.getUserById(id);
        model.addAttribute("User",user);
        Roles roles = userService.getRoleById(role_id);
        if (roles != null) {
            if (user != null) {
                List<Roles> rolesList = user.getRoles();
                if (rolesList == null) {
                    rolesList = new ArrayList<>();
                }
                rolesList.add(roles);
                model.addAttribute("AllRoles",userService.findAllRoles());
                //items.setCategories(categoriesList);
                userService.saveUser(user);
            }
        }
        return "adminpanel_UserDetails";
    }

    @GetMapping(value = "/403")
    public String accessDenied(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "403";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model) {
        model.addAttribute("currentUser", getUserData());
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        return "user_profile";
    }
    @GetMapping(value = "/basket")
    public String basket(Model model,
                         HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("currentUser", getUserData());
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        Cookie[] cookies = request.getCookies();
        ArrayList<Items> basketitems=new ArrayList<>();
        ArrayList<Integer> basketitems_amount=new ArrayList<>();
        double total=0;
        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(isNumeric(c.getName()) && isNumeric(c.getValue())) {
                   basketitems.add(itemService.getItem(Long.parseLong(c.getName())));
                   basketitems_amount.add(Integer.parseInt(c.getValue()));
                   total+=itemService.getItem(Long.parseLong(c.getName())).getPrice()*
                           Integer.parseInt(c.getValue());
                }
            }
        }
        model.addAttribute("BasketItems", basketitems);
        model.addAttribute("BasketItems_Amount", basketitems_amount);
        model.addAttribute("Total", total);
        return "BasketPage";
    }


    @PostMapping(value = "/tobasket")
    public String tobasket(@RequestParam(name = "item_id") Long item_id,
                           HttpServletRequest request, HttpServletResponse response){
        boolean access=true;
        Cookie[] cookies = request.getCookies();
        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(isNumeric(c.getName()) && String.valueOf(item_id).equals(c.getName())) {
                    c.setValue(String.valueOf(Long.parseLong(c.getValue())+1));
                    response.addCookie(c);
                    access=false;
                    break;
                }
            }
            if(access){
                response.addCookie(new Cookie(String.valueOf(item_id), "1"));
            }
        }
        return "redirect:/basket";
    }

    @PostMapping(value = "/decreasebasket")
    public String decreasebasket(@RequestParam(name = "item_id") Long item_id,
                           HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(isNumeric(c.getName()) && String.valueOf(item_id).equals(c.getName())) {
                    if(c.getValue().equals("1")){
                        c.setMaxAge(0);
                    }
                    else{
                        c.setValue(String.valueOf(Long.parseLong(c.getValue())-1));
                    }
                    response.addCookie(c);
                    break;
                }
            }
        }
        return "redirect:/basket";
    }

    @PostMapping(value = "/clearbasket")
    public String clearbasket(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(isNumeric(c.getName()) && isNumeric(c.getValue())) {
                    c.setMaxAge(0);
                    response.addCookie(c);
                }
            }
        }
        return "redirect:/basket";
    }

    @PostMapping(value = "/comfirmpurchase")
    public String comfirmpurchase(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(isNumeric(c.getName()) && isNumeric(c.getValue())) {
                    Purchase_history purchase_history=new Purchase_history();
                    purchase_history.setItem_id(Long.parseLong(c.getName()));
                    purchase_history.setAmount(Long.parseLong(c.getValue()));
                    purchase_history.setAddedDate(new Date(Calendar.getInstance().getTimeInMillis()));
                    itemService.addPurchase(purchase_history);
                    c.setMaxAge(0);
                    response.addCookie(c);
                }
            }
        }
        return "redirect:/basket";
    }

    private static boolean isNumeric(final String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    @PostMapping(value = "/addcomment")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR','ROLE_USER')")
    public String addcomment(@RequestParam(name = "item_id", defaultValue = "") Long item_id,
                             @RequestParam(name = "user_id", defaultValue = "") Long user_id,
                             @RequestParam(name = "comment", defaultValue = "") String comment){
        Comments comments = new Comments();
        comments.setAuthor(userService.getUserById(user_id));
        comments.setComment(comment);
        comments.setItem(itemService.getItem(item_id));
        comments.setAddedDate(new Timestamp(System.currentTimeMillis()));
        itemService.addComment(comments);
        return "redirect:/details/"+item_id;
    }
    @PostMapping(value = "/deletecomment")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR','ROLE_USER')")
    public String deletecomment(@RequestParam(name = "comment_id", defaultValue = "") Long comment_id,
                             @RequestParam(name = "item_id", defaultValue = "") Long item_id){
        itemService.deleteComment(comment_id);
        return "redirect:/details/"+item_id;
    }
    @PostMapping(value = "/editcomment")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR','ROLE_USER')")
    public String editcomment(@RequestParam(name = "comment_id", defaultValue = "") Long comment_id,
                                @RequestParam(name = "item_id", defaultValue = "") Long item_id,
                              @RequestParam(name = "comment", defaultValue = "") String comment){
        Comments comments=itemService.getOneComment(comment_id);
        comments.setComment(comment);
        itemService.saveComment(comments);
        return "redirect:/details/"+item_id;
    }



    private Users getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User) authentication.getPrincipal();
            Users myUser = userService.getUserByEmail(secUser.getUsername());
            return myUser;
        }
        return null;
    }

    //-------------------------------

    @GetMapping(value = "/search")
    public String search(Model model,
                         @RequestParam(name = "search_value", defaultValue = "") String search_value,
                         @RequestParam(name = "brand", defaultValue = "-1") Long brand_id,
                         @RequestParam(name = "price_from", defaultValue = "0") double price_from,
                         @RequestParam(name = "price_to", defaultValue = "1000000") double price_to,
                         @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                         @RequestParam(name = "desc", defaultValue = "false") boolean desc) {
        List<Items> shopItems = new ArrayList<>();
        if (!desc && brand_id != -1) {
            shopItems = itemService.getSearchBrandItemsAsc(search_value, itemService.getBrand(brand_id), price_from, price_to);
        } else if (desc && brand_id != -1) {
            shopItems = itemService.getSearchBrandItemsDesc(search_value, itemService.getBrand(brand_id), price_from, price_to);
        } else if (!desc) {
            shopItems = itemService.getSearchItemsAsc(search_value, price_from, price_to);
        } else {
            shopItems = itemService.getSearchItemsDesc(search_value, price_from, price_to);
        }
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("Items", shopItems);
        model.addAttribute("search_value", search_value);
        model.addAttribute("brand_id", brand_id);
        model.addAttribute("price_from", price_from);
        model.addAttribute("price_to", price_to);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        return "search";
    }

    @PostMapping(value = "/adminpanelItems")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String adminpanelItems(Model model) {
        List<Items> shopItems = itemService.getAllItems();
        model.addAttribute("Items", shopItems);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        return "adminpanel_Item";
    }

    @RequestMapping(value = "/adminpanelItemsDetails/{id}")
    public String ItemDetails(Model model, @PathVariable(name = "id") Long id) {
        Items items = itemService.getItem(id);
        model.addAttribute("item", items);
        List<Items> shopItems = itemService.getAllItems();
        model.addAttribute("Items", shopItems);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("Categories", categoriesList);
        List<Pictures> pictures=pictureService.getAllpicturesbByItem(items);
        model.addAttribute("Pictures",pictures);
        return "adminpanel_ItemDetails";
    }

    @PostMapping(value = "/assigncategory")
    public String assigncategory(Model model,
                                 @RequestParam(name = "item_id") Long item_id,
                                 @RequestParam(name = "category_id") Long category_id) {

        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        List<Categories> categoriesList1 = itemService.getAllCategories();
        model.addAttribute("Categories", categoriesList1);

        Categories categories = itemService.getCategory(category_id);
        if (categories != null) {
            Items items = itemService.getItem(item_id);
            if (items != null) {
                List<Categories> categoriesList = items.getCategories();
                if (categoriesList == null) {
                    categoriesList = new ArrayList<>();
                }
                categoriesList.add(categories);
                itemService.saveItem(items);
                return "redirect:/adminpanelItemsDetails/" + item_id;
            }
        }
        return "redirect:/adminpanel_Item";
    }

    @PostMapping(value = "/removecategory")
    public String deletecategory(Model model,
                                 @RequestParam(name = "item_id") Long item_id,
                                 @RequestParam(name = "category_id") Long category_id) {

        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        List<Categories> categoriesList1 = itemService.getAllCategories();
        model.addAttribute("Categories", categoriesList1);

        Categories categories = itemService.getCategory(category_id);
        if (categories != null) {
            Items items = itemService.getItem(item_id);
            if (items != null) {
                List<Categories> categoriesList = items.getCategories();
                if (categoriesList == null) {
                    categoriesList = new ArrayList<>();
                }
                categoriesList.remove(categories);
                //items.setCategories(categoriesList);
                itemService.saveItem(items);
                return "redirect:/adminpanelItemsDetails/" + item_id;
            }
        }
        return "redirect:/adminpanel_Item";
    }

    @PostMapping(value = "/adminpanelBrands")
    public String adminpanelBrands(Model model) {
        List<Items> shopItems = itemService.getAllItems();
        model.addAttribute("Items", shopItems);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brands);
        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("Countries", countries);
        return "adminpanel_Brand";
    }


    @PostMapping(value = "/adminpanelCountries")
    public String adminpanelCountries(Model model) {
        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("Countries", countries);
        return "adminpanel_Country";
    }

    @PostMapping(value = "/adminpanelCategories")
    public String adminpanelCategories(Model model) {
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("Categories", categoriesList);
        return "adminpanel_Category";
    }

    @PostMapping(value = "/adminpanelUsers")
    public String adminpanelUsers(Model model) {
        List<Users> users=userService.getAllByOrderByIdAsc();
        model.addAttribute("Users",users);
        return "adminpanel_User";
    }
    @PostMapping(value = "/adminpanelPurchases")
    public String adminpanelPurchases(Model model) {
        List<Purchase_history> purchase_histories=itemService.getAllPurchases();
        model.addAttribute("Purchases",purchase_histories);
        ArrayList<Items> items=new ArrayList<>();
        for (Purchase_history p:purchase_histories) {
            items.add(itemService.getItem(p.getItem_id()));
        }
        model.addAttribute("Items",items);
        return "adminpanel_Purchases";
    }



    @GetMapping(value = "/details/{id}")
    //@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR','ROLE_USER')")
    public String details(Model model, @PathVariable(name = "id") Long id) {
        Items item = itemService.getItem(id);
        model.addAttribute("item", item);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("Item_Brands", brands);
        List<Pictures> picturess=pictureService.getAllpicturesbByItem(item);
        model.addAttribute("Pictures",picturess);
        List<Comments> comments=itemService.getAllCommentsbyItem(item);
        model.addAttribute("Comments",comments);
        return "details";
    }

    //---------------------------------------------------------------------

    @PostMapping(value = "/additem")
    public String add(Model model,
                      @RequestParam(name = "name") String name,
                      @RequestParam(name = "description") String description,
                      @RequestParam(name = "price") double price,
                      @RequestParam(name = "amount") int amount,
                      @RequestParam(name = "stars") int stars,
                      @RequestParam(name = "pictureUrl") String pictureUrl,
                      @RequestParam(name = "brand", defaultValue = "1") Long brand_id,
                      @RequestParam(name = "category", defaultValue = "1") Long category_id,
                      @RequestParam(name = "inTop", defaultValue = "false") boolean inTop) {
        //itemService.addItem(new Items(null,name,description, price,stars,amount,pictureUrl,pictureUrl,new Date(Calendar.getInstance().getTimeInMillis()),inTop,itemService.getBrand(brand_id)));
        Items item = new Items();
        item.setId(null);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setStars(stars);
        item.setAmount(amount);
        item.setSmallPicURL(pictureUrl);
        item.setLargePicURL(pictureUrl);
        item.setAddedDate(new Date(Calendar.getInstance().getTimeInMillis()));
        item.setInTopPage(inTop);
        item.setBrand(itemService.getBrand(brand_id));
        //item.setCategories();
        itemService.addItem(item);

        List<Brands> brands = itemService.getAllBrands();
        List<Items> shopItems = itemService.getAllItems();
        model.addAttribute("Items", shopItems);
        model.addAttribute("Item_Brands", brands);
        return "adminpanel_Item";
    }

    @PostMapping(value = "/edit")
    public String edit(Model model,
                       @RequestParam(name = "id") Long id,
                       @RequestParam(name = "item_name") String name,
                       @RequestParam(name = "description") String description,
                       @RequestParam(name = "price") double price,
                       @RequestParam(name = "amount") int amount,
                       @RequestParam(name = "stars") int stars,
                       @RequestParam(name = "pictureUrl") String pictureUrl,
                       @RequestParam(name = "brand") Long brand_id,
                       @RequestParam(name = "inTop", defaultValue = "false") boolean inTop) {

        Items item = itemService.getItem(id);
        if (item != null) {
            item.setAddedDate(new Date(Calendar.getInstance().getTimeInMillis()));
            item.setAmount(amount);
            item.setDescription(description);
            item.setInTopPage(inTop);
            item.setLargePicURL(pictureUrl);
            item.setSmallPicURL(pictureUrl);
            item.setName(name);
            item.setStars(stars);
            item.setPrice(price);
            item.setBrand(itemService.getBrand(brand_id));
            itemService.saveItem(item);
        }
        List<Items> shopItems = itemService.getAllItems();
        model.addAttribute("Items", shopItems);
        return "adminpanel_Item";
    }

    @PostMapping(value = "/delete")
    public String delete(Model model,
                         @RequestParam(name = "id") Long id) {
        itemService.deleteItemById(id);
        List<Items> shopItems = itemService.getAllItems();
        model.addAttribute("Items", shopItems);
        return "adminpanel_Item";
    }

    //---------------------------------------------------------------------

    @PostMapping(value = "/addcategory")
    public String add(Model model,
                      @RequestParam(name = "name") String name,
                      @RequestParam(name = "logoURL", defaultValue = "") String logoURL) {
        Categories categories = new Categories();
        categories.setName(name);
        categories.setLogoURL(logoURL);
        itemService.saveCategory(categories);

        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("Categories", categoriesList);

        return "adminpanel_Category";
    }

    @PostMapping(value = "/editcategory")
    public String editcategory(Model model,
                               @RequestParam(name = "id") Long id,
                               @RequestParam(name = "name") String name,
                               @RequestParam(name = "logoURL", defaultValue = "") String logoURL) {
        Categories categories = itemService.getCategory(id);
        categories.setName(name);
        categories.setLogoURL(logoURL);
        itemService.saveCategory(categories);

        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("Categories", categoriesList);

        return "adminpanel_Category";
    }

    @PostMapping(value = "/deletecategory")
    public String deletecategory(Model model,
                                 @RequestParam(name = "id") Long id) {
        itemService.deleteCategory(id);
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("Categories", categoriesList);
        return "adminpanel_Category";
    }

    //---------------------------------------------------------------------

    @PostMapping(value = "/addcountry")
    public String addcountry(Model model,
                             @RequestParam(name = "name") String name,
                             @RequestParam(name = "code") String code) {
        Countries countries = new Countries();
        countries.setName(name);
        countries.setCode(code);
        itemService.saveCountry(countries);

        List<Countries> countriesList = itemService.getAllCountries();
        model.addAttribute("Countries", countriesList);

        return "adminpanel_Country";
    }

    @PostMapping(value = "/editcountry")
    public String editcountry(Model model,
                              @RequestParam(name = "id") Long id,
                              @RequestParam(name = "name") String name,
                              @RequestParam(name = "code") String code) {
        Countries countries = itemService.getCountry(id);
        countries.setName(name);
        countries.setCode(code);
        itemService.saveCountry(countries);

        List<Countries> countriesList = itemService.getAllCountries();
        model.addAttribute("Countries", countriesList);

        return "adminpanel_Country";
    }

    @PostMapping(value = "/deletecountry")
    public String deletecountry(Model model,
                                @RequestParam(name = "id") Long id) {
        itemService.deleteCountry(id);
        List<Countries> countriesList = itemService.getAllCountries();
        model.addAttribute("Countries", countriesList);

        return "adminpanel_Country";
    }
    //---------------------------------------------------------------------

    @PostMapping(value = "/addbrand")
    public String addbrand(Model model,
                           @RequestParam(name = "name") String name,
                           @RequestParam(name = "country") Long country_id) {
        Brands brands = new Brands();
        brands.setName(name);
        brands.setCountries(itemService.getCountry(country_id));
        itemService.saveBrand(brands);

        List<Items> shopItems = itemService.getAllItems();
        model.addAttribute("Items", shopItems);
        List<Brands> brandsList = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brandsList);
        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("Countries", countries);

        return "adminpanel_Brand";
    }

    @PostMapping(value = "/editbrand")
    public String editbrand(Model model,
                            @RequestParam(name = "id") Long id,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "country") Long country_id) {
        Brands brands = itemService.getBrand(id);
        brands.setName(name);
        brands.setCountries(itemService.getCountry(country_id));
        itemService.saveBrand(brands);

        List<Brands> brandsList = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brandsList);
        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("Countries", countries);

        return "adminpanel_Brand";
    }

    @PostMapping(value = "/deletebrand")
    public String deletebrand(Model model,
                              @RequestParam(name = "id") Long id) {
        itemService.deleteBrand(id);
        List<Brands> brandsList = itemService.getAllBrands();
        model.addAttribute("Item_Brands", brandsList);
        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("Countries", countries);

        return "adminpanel_Brand";
    }

}
