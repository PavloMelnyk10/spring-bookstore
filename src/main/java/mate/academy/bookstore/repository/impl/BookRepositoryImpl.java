package mate.academy.bookstore.repository.impl;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.exception.DataProcessingException;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.BookRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final EntityManager entityManager;

    @Override
    @Transactional
    public Book save(Book book) {
        try {
            entityManager.persist(book);
            return book;
        } catch (Exception e) {
            throw new DataProcessingException("Failed to add book: " + book, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        try {
            return entityManager.createQuery("FROM Book", Book.class).getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't get all books", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Book findById(Long id) {
        Book book = entityManager.find(Book.class, id);
        if (book == null) {
            throw new EntityNotFoundException("Can't find book with id: " + id);
        }
        return book;
    }
}
