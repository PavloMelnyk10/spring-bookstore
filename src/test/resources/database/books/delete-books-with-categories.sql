DELETE FROM books_categories
WHERE (book_id = 1 AND category_id = 1)
   OR (book_id = 2 AND category_id IN (1, 2));

DELETE FROM books
WHERE id IN (1, 2);