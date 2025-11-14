-- V2__create_transactions_tables.sql

-- Predefined categories table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('EXPENSE', 'INCOME')),
    icon VARCHAR(50),
    color VARCHAR(7),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert predefined categories
INSERT INTO categories (name, type, icon, color) VALUES
    ('Food & Dining', 'EXPENSE', '🍔', '#FF6B6B'),
    ('Transportation', 'EXPENSE', '🚗', '#4ECDC4'),
    ('Shopping', 'EXPENSE', '🛍️', '#95E1D3'),
    ('Entertainment', 'EXPENSE', '🎬', '#F38181'),
    ('Healthcare', 'EXPENSE', '🏥', '#AA96DA'),
    ('Bills & Utilities', 'EXPENSE', '💡', '#FCBAD3'),
    ('Education', 'EXPENSE', '📚', '#A8D8EA'),
    ('Groceries', 'EXPENSE', '🛒', '#FFD93D'),
    ('Travel', 'EXPENSE', '✈️', '#6BCB77'),
    ('Other Expenses', 'EXPENSE', '📦', '#B8B8B8'),
    ('Salary', 'INCOME', '💰', '#4D96FF'),
    ('Freelance', 'INCOME', '💼', '#6BCB77'),
    ('Investments', 'INCOME', '📈', '#FFD93D'),
    ('Other Income', 'INCOME', '💵', '#95E1D3');

-- Transactions table
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount DECIMAL(12, 2) NOT NULL,
    description TEXT NOT NULL,
    transaction_date DATE NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('EXPENSE', 'INCOME')),
    payment_method VARCHAR(50),
    ai_comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_user ON transactions(user_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_category ON transactions(category_id);
CREATE INDEX idx_transactions_type ON transactions(type);