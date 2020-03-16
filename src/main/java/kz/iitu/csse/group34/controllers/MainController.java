package kz.iitu.csse.group34.controllers;

import kz.iitu.csse.group34.entities.*;
import kz.iitu.csse.group34.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ItemsRepository itemsRepository;
    @Autowired
    private BooksRepository booksRepository;
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private GenresRepository genresRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/")
    public String index(ModelMap model, @RequestParam(name = "page", defaultValue = "1") int page){

        int pageSize = 10;

        if(page<1){
            page = 1;
        }

        int totalItems = booksRepository.countAllByDeletedAtNull();
        int tabSize = (totalItems+pageSize-1)/pageSize;

        Pageable pageable = PageRequest.of(page-1, pageSize);
        List<Books> books = booksRepository.findAllByDeletedAtNull(pageable);
        model.addAttribute("books", books);
        model.addAttribute("tabSize", tabSize);
        return "index";
    }
    @GetMapping(value = "/registration")
    public String registration(ModelMap model){

        return "registration";
    }
    @PostMapping(value = "/addUser")
    public String addUser(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "rePassword") String rePassword,
            @RequestParam(name = "fullName") String fullName
    ){
        HashSet<Roles> roles = new HashSet<>();
        roles.add(rolesRepository.findById(2L).orElse(null));
        Users user = new Users(null, email, passwordEncoder.encode(password), fullName, roles);
        userRepository.save(user);
        return "redirect:/";
    }
    @GetMapping(path = "/addBook")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addBook(ModelMap model){

        List<Genres> genres = genresRepository.findAll();
        model.addAttribute("genres", genres);

        return "addBook";
    }
    @PostMapping(value = "/addBook")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addBook(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "price") int price,
            @RequestParam(name = "author") String author,
            @RequestParam(name = "genre") Long [] genre
    ){
        HashSet<Genres> genres = new HashSet<>();
//        String[] arr = genre.split(",");
//        System.out.println(genre);
//        System.out.println(arr);
        for(int i =0;i<genre.length;i++){
            genres.add(genresRepository.findById(genre[i]).get());
        }
        Books book = new Books(name, price, author, new Date(), genres);
        booksRepository.save(book);

        return "redirect:/";
    }

    @PostMapping(path = "/addOrder")
    @PreAuthorize("isAuthenticated()")
    public String addOrder(@RequestParam(name = "id") Long id){
        Books book = booksRepository.findByIdAndDeletedAtNull(id).get();
        Orders order = new Orders(book, getUserData(), 1);
        ordersRepository.save(order);
        return "redirect:/";
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String add(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "price") int price
    ){

        Items item = new Items(name, price);
        itemsRepository.save(item);

        return "redirect:/";
    }

    @GetMapping(path = "/details/{id}")
    public String details(ModelMap model, @PathVariable(name = "id") Long id){

        Optional<Books> book = booksRepository.findByIdAndDeletedAtNull(id);
        model.addAttribute("book", book.orElse(new Books("No Name", 0, "null", new Date() ,null)));

        return "details";
    }

    @PostMapping(path = "/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete(@RequestParam(name = "id") Long id){
        Items item = itemsRepository.findByIdAndDeletedAtNull(id).get();
        item.setDeletedAt(new Date());
        itemsRepository.save(item);
        return "redirect:/";
    }

    @GetMapping(path = "/login")
    public String loginPage(Model model){

        return "login";

    }

    @GetMapping(path = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model){
        Users user = getUserData();
        List<Orders> orders = ordersRepository.findAllByUser_IdAndDeletedAtNull(user.getId());
        model.addAttribute("orders", orders);
        model.addAttribute("user", getUserData());
        return "profile";

    }

    @GetMapping(path = "/users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String usersPage(Model model){

        model.addAttribute("user", getUserData());

        List<Users> users = userRepository.findAll();
        model.addAttribute("userList", users);

        return "users";

    }

    public Users getUserData(){
        Users userData = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            User secUser = (User)authentication.getPrincipal();
            userData = userRepository.findByEmail(secUser.getUsername());
        }
        return userData;
    }

}