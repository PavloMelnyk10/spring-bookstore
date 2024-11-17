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
            Book book1 = new Book(null, "Spring Boot Essentials", "John Smith",
                    "1234567890123", new BigDecimal("19.99"),
                    "A great introduction to Spring Boot", "coverImage1.jpg");
            Book book2 = new Book(null, "Learning Java", "Jane Doe",
                    "2345678901234", new BigDecimal("29.99"),
                    "A comprehensive guide to Java programming", "coverImage2.jpg");
            Book book3 = new Book(null, "Mastering Hibernate", "Mark Johnson",
                    "3456789012345", new BigDecimal("39.99"),
                    "An in-depth look at Hibernate ORM", "coverImage3.jpg");

            bookService.save(book1);
            bookService.save(book2);
            bookService.save(book3);
        };
    }
}
