INSERT INTO books (id, title, author, isbn, price, description, cover_image)
VALUES (1, 'Harry Potter', 'J.K. Rowling', '978-1234567897', 14.99, 'A young wizard''s adventures.', 'https://example.com/hp.jpg')
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE book_id = book_id;
