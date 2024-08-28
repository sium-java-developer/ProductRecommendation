-- Retrieve all products
SELECT * FROM products;

-- Get ratings for a specific user
SELECT * FROM ratings WHERE user_id = 1;

-- Calculate average rating for a product
SELECT AVG(rating) FROM ratings WHERE product_id = 2;

-- Find similar products based on ratings (collaborative filtering)
SELECT products.product_id, products.name
FROM products
JOIN ratings ON products.product_id = ratings.product_id
WHERE ratings.user_id = 1
GROUP BY products.product_id
ORDER BY AVG(ratings.rating) DESC
LIMIT 5;
