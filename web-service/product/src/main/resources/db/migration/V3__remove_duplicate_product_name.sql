DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'products'
          AND column_name = 'product_name'
    ) THEN
        UPDATE products SET name = product_name WHERE product_name IS NOT NULL;
        ALTER TABLE products DROP COLUMN product_name;
    END IF;
END $$;
