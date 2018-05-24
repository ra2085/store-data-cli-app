package com.rgonzalez.dao;

/**
 * This interface will allow to have multiple data storage implementations.
 * @author Ruben Antonio Gonzalez Saldierna
 */
public interface TransactionDAO {
    
    String addTransaction(Long userId, String transaction) throws Exception;
    
    String showTransaction(Long userId, String transactionId) throws Exception;
    
    String listTransactions(Long userId) throws Exception;
    
    String sumTransactions(Long userId) throws Exception;
    
}
