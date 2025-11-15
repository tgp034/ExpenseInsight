package com.expenseinsight.api.entity.enums;

/**
 * Enumeration representing the type of financial transaction.
 * 
 * <p>Transactions in ExpenseInsight can be classified as either expenses (money spent)
 * or income (money received).</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
public enum TransactionType {
    /**
     * Represents money spent or outgoing transactions.
     */
    EXPENSE,
    
    /**
     * Represents money received or incoming transactions.
     */
    INCOME
}