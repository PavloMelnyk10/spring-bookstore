INSERT INTO books (id, title, author, isbn, price, description, cover_image)
VALUES
    (1, 'Harry Potter', 'J.K. Rowling', '978-1234567897', 14.99, 'A wizard story', 'https://example.com/hp.jpg'),
    (2, 'The Hobbit', 'J.R.R. Tolkien', '978-9876543210', 12.49, 'A journey story', 'https://example.com/hobbit.jpg');

INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1), (2, 1), (2, 2);