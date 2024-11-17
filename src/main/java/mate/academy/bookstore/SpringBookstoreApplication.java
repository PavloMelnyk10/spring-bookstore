package mate.academy.bookstore;

import java.math.BigDecimal;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBookstoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(SpringBookstoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book bookOne = new Book();
            bookOne.setTitle("Spring Boot Essentials");
            bookOne.setAuthor("John Smith");
            bookOne.setIsbn("1234567890123");
            bookOne.setPrice(new BigDecimal("19.99"));
            bookOne.setDescription("A great introduction to Spring Boot");
            bookOne.setCoverImage("coverImage1.jpg");

            Book bookTwo = new Book();
            bookTwo.setTitle("Learning Java");
            bookTwo.setAuthor("Jane Doe");
            bookTwo.setIsbn("2345678901234");
            bookTwo.setPrice(new BigDecimal("29.99"));
            bookTwo.setDescription("A comprehensive guide to Java programming");
            bookTwo.setCoverImage("coverImage2.jpg");

            Book bookThree = new Book();
            bookThree.setTitle("Mastering Hibernate");
            bookThree.setAuthor("Mark Johnson");
            bookThree.setIsbn("3456789012345");
            bookThree.setPrice(new BigDecimal("39.99"));
            bookThree.setDescription("An in-depth look at Hibernate ORM");
            bookThree.setCoverImage("coverImage3.jpg");

            bookService.save(bookOne);
            bookService.save(bookTwo);
            bookService.save(bookThree);
        };
    }
}
