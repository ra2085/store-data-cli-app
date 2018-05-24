package com.rgonzalez.exceptions;

/**
 *
 * @author Ruben Antonio Gonzalez Saldierna
 */
public class TransactionNotFoundException extends Exception{

    public TransactionNotFoundException() {
        super("Transaction not found");
    }
    
    
}
