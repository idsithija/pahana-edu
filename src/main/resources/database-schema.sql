-- Pahana Edu Bookshop Database Schema
-- PostgreSQL 13+ compatible

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS bill_items CASCADE;
DROP TABLE IF EXISTS bills CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table for authentication
CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'OPERATOR')),
    active BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers table
CREATE TABLE customers (
    account_number VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    telephone_number VARCHAR(20),
    registration_date DATE DEFAULT CURRENT_DATE,
    active BOOLEAN DEFAULT true
);

-- Items table
CREATE TABLE items (
    item_id BIGSERIAL PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    description TEXT,
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    stock_quantity INTEGER DEFAULT 0 CHECK (stock_quantity >= 0),
    category VARCHAR(50)
);

-- Bills table
CREATE TABLE bills (
    bill_id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) REFERENCES customers(account_number),
    bill_date DATE DEFAULT CURRENT_DATE,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PAID', 'CANCELLED'))
);

-- Bill items (many-to-many relationship between bills and items)
CREATE TABLE bill_items (
    bill_item_id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT REFERENCES bills(bill_id) ON DELETE CASCADE,
    item_id BIGINT REFERENCES items(item_id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0)
);

-- Indexes for better performance
CREATE INDEX idx_customers_name ON customers(name);
CREATE INDEX idx_items_name ON items(item_name);
CREATE INDEX idx_bills_date ON bills(bill_date);
CREATE INDEX idx_bills_customer ON bills(account_number);

-- Function to generate unique account numbers
CREATE OR REPLACE FUNCTION generate_account_number()
RETURNS VARCHAR(20) AS $$
DECLARE
    new_account_number VARCHAR(20);
    counter INTEGER := 1;
BEGIN
    LOOP
        new_account_number := 'ACC' || LPAD(counter::TEXT, 6, '0');
        
        -- Check if this account number already exists
        IF NOT EXISTS (SELECT 1 FROM customers WHERE account_number = new_account_number) THEN
            RETURN new_account_number;
        END IF;
        
        counter := counter + 1;
        
        -- Safety check to prevent infinite loop
        IF counter > 999999 THEN
            RAISE EXCEPTION 'Unable to generate unique account number';
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update stock quantity when items are sold
CREATE OR REPLACE FUNCTION update_stock_on_bill_item()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- Decrease stock when bill item is added
        UPDATE items 
        SET stock_quantity = stock_quantity - NEW.quantity 
        WHERE item_id = NEW.item_id;
        
        -- Check if stock goes negative
        IF (SELECT stock_quantity FROM items WHERE item_id = NEW.item_id) < 0 THEN
            RAISE EXCEPTION 'Insufficient stock for item ID %', NEW.item_id;
        END IF;
        
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        -- Increase stock when bill item is removed
        UPDATE items 
        SET stock_quantity = stock_quantity + OLD.quantity 
        WHERE item_id = OLD.item_id;
        
        RETURN OLD;
    ELSIF TG_OP = 'UPDATE' THEN
        -- Adjust stock based on quantity change
        UPDATE items 
        SET stock_quantity = stock_quantity + OLD.quantity - NEW.quantity 
        WHERE item_id = NEW.item_id;
        
        -- Check if stock goes negative
        IF (SELECT stock_quantity FROM items WHERE item_id = NEW.item_id) < 0 THEN
            RAISE EXCEPTION 'Insufficient stock for item ID %', NEW.item_id;
        END IF;
        
        RETURN NEW;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for stock updates
CREATE TRIGGER trigger_update_stock
    AFTER INSERT OR UPDATE OR DELETE ON bill_items
    FOR EACH ROW
    EXECUTE FUNCTION update_stock_on_bill_item();

-- Stored procedure to calculate bill total
CREATE OR REPLACE FUNCTION calculate_bill_total(bill_id_param INTEGER)
RETURNS DECIMAL(10,2) AS $$
DECLARE
    total_amount DECIMAL(10,2);
BEGIN
    SELECT COALESCE(SUM(total_price), 0)
    INTO total_amount
    FROM bill_items
    WHERE bill_id = bill_id_param;
    
    -- Update the bill with calculated total
    UPDATE bills 
    SET total_amount = total_amount 
    WHERE bill_id = bill_id_param;
    
    RETURN total_amount;
END;
$$ LANGUAGE plpgsql;

-- Insert default admin user (password: admin123) - Academic-compliant SHA-256 hash
INSERT INTO users (username, password_hash, role) VALUES 
('admin', 'mm/V9vMfeZk3wPHLWO8EQum2u0OWBWpznJwkq5kkPnf9ne5SH8bzzAElM5KZlZR4', 'ADMIN');

-- Insert sample operator user (password: operator123) - Academic-compliant SHA-256 hash
INSERT INTO users (username, password_hash, role) VALUES 
('operator', '+nLoc6lV8eoDKEt75xl9s0dqFCNJuTKc9ilGFsY3rwMPQh1At/fDlLttXIfB8b9p', 'OPERATOR');

-- Insert sample customers
INSERT INTO customers (account_number, name, address, telephone_number) VALUES 
('ACC000001', 'John Doe', '123 Main Street, Colombo 01', '+94771234567'),
('ACC000002', 'Jane Smith', '456 Galle Road, Colombo 03', '+94772345678'),
('ACC000003', 'Michael Johnson', '789 Kandy Road, Colombo 07', '+94773456789');

-- Insert sample items
INSERT INTO items (item_name, description, unit_price, stock_quantity, category) VALUES 
('Java Programming Book', 'Complete guide to Java programming', 2500.00, 50, 'Programming'),
('Mathematics Textbook', 'Advanced mathematics for university students', 3200.00, 30, 'Mathematics'),
('English Grammar Guide', 'Comprehensive English grammar reference', 1800.00, 40, 'Language'),
('Physics Laboratory Manual', 'Practical physics experiments guide', 2800.00, 25, 'Science'),
('History of Sri Lanka', 'Complete history of Sri Lankan civilization', 2200.00, 35, 'History');

-- Insert sample bills
INSERT INTO bills (account_number, total_amount, status) VALUES 
('ACC000001', 5000.00, 'PAID'),
('ACC000002', 3200.00, 'PENDING'),
('ACC000003', 4000.00, 'PAID');

-- Insert sample bill items
INSERT INTO bill_items (bill_id, item_id, quantity, unit_price, total_price) VALUES 
(1, 1, 2, 2500.00, 5000.00),
(2, 2, 1, 3200.00, 3200.00),
(3, 3, 1, 1800.00, 1800.00),
(3, 4, 1, 2200.00, 2200.00);

-- Grant permissions to application user
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO pahanaedu_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO pahanaedu_user;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO pahanaedu_user;

