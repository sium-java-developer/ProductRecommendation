

-- Insert sample data into the products table
INSERT INTO products (name, description)
VALUES
    ('Product A', 'This is a sample product description.'),
    ('Product B', 'Another sample product description.'),
    ('Product C', 'Yet another sample product description.');

-- Insert sample data into the users table
INSERT INTO users (username)
VALUES
    ('user1'),
    ('user2'),
    ('user3');

-- Insert sample data into the ratings table
INSERT INTO ratings (user_id, product_id, rating)
VALUES
    (1, 1, 4),
    (1, 2, 3),
    (2, 1, 5),
    (2, 3, 2),
    (3, 2, 4);
